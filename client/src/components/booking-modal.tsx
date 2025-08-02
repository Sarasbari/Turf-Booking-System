import { useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useToast } from "@/hooks/use-toast";
import { isUnauthorizedError } from "@/lib/authUtils";
import PaymentModal from "./payment-modal";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";
import { ChevronLeft, ChevronRight, X } from "lucide-react";
import type { Turf } from "@shared/schema";

interface BookingModalProps {
  turf: Turf;
  isOpen: boolean;
  onClose: () => void;
}

export default function BookingModal({ turf, isOpen, onClose }: BookingModalProps) {
  const { user } = useAuth();
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [selectedTime, setSelectedTime] = useState<string>("");
  const [isPaymentOpen, setIsPaymentOpen] = useState(false);
  const [bookingId, setBookingId] = useState<string>("");

  const dateString = selectedDate.toISOString().split('T')[0];

  const { data: availability } = useQuery({
    queryKey: ["/api/turfs", turf.id, "availability", dateString],
    queryFn: async () => {
      const response = await fetch(`/api/turfs/${turf.id}/availability/${dateString}`);
      if (!response.ok) throw new Error("Failed to fetch availability");
      return response.json();
    },
    enabled: isOpen,
  });

  const createBookingMutation = useMutation({
    mutationFn: async (bookingData: any) => {
      const response = await apiRequest("POST", "/api/bookings", bookingData);
      return response.json();
    },
    onSuccess: (booking) => {
      setBookingId(booking.id);
      setIsPaymentOpen(true);
      queryClient.invalidateQueries({ queryKey: ["/api/bookings"] });
    },
    onError: (error) => {
      if (isUnauthorizedError(error)) {
        toast({
          title: "Unauthorized",
          description: "You are logged out. Logging in again...",
          variant: "destructive",
        });
        setTimeout(() => {
          window.location.href = "/api/login";
        }, 500);
        return;
      }
      toast({
        title: "Booking Failed",
        description: "Failed to create booking. Please try again.",
        variant: "destructive",
      });
    },
  });

  const timeSlots = [];
  for (let hour = 6; hour <= 22; hour++) {
    const timeSlot = `${hour.toString().padStart(2, '0')}:00`;
    timeSlots.push(timeSlot);
  }

  const isSlotAvailable = (slot: string) => {
    return availability?.availableSlots?.includes(slot) ?? false;
  };

  const isSlotBooked = (slot: string) => {
    return availability?.bookedSlots?.includes(slot) ?? false;
  };

  const handleProceedToPayment = () => {
    if (!selectedTime) {
      toast({
        title: "Select Time Slot",
        description: "Please select a time slot to proceed.",
        variant: "destructive",
      });
      return;
    }

    const endHour = parseInt(selectedTime.split(':')[0]) + 1;
    const endTime = `${endHour.toString().padStart(2, '0')}:00`;

    const bookingData = {
      turfId: turf.id,
      bookingDate: selectedDate,
      startTime: selectedTime,
      endTime,
      duration: 1,
      totalAmount: turf.pricePerHour,
      status: 'pending',
      paymentStatus: 'pending',
    };

    createBookingMutation.mutate(bookingData);
  };

  const totalAmount = parseFloat(turf.pricePerHour);
  const platformFee = 50;
  const gst = Math.round((totalAmount + platformFee) * 0.18);
  const finalAmount = totalAmount + platformFee + gst;

  return (
    <>
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="max-w-4xl max-h-screen overflow-y-auto">
          <DialogHeader>
            <div className="flex items-center justify-between">
              <DialogTitle className="text-2xl">Book {turf.name}</DialogTitle>
              <Button variant="ghost" size="sm" onClick={onClose}>
                <X className="w-5 h-5" />
              </Button>
            </div>
          </DialogHeader>
          
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 p-6">
            {/* Calendar Section */}
            <div>
              <h4 className="text-lg font-semibold text-gray-900 mb-4">Select Date</h4>
              <div className="bg-gray-50 rounded-lg p-4">
                <Calendar
                  mode="single"
                  selected={selectedDate}
                  onSelect={(date) => date && setSelectedDate(date)}
                  disabled={(date) => date < new Date() || date < new Date("1900-01-01")}
                  className="w-full"
                />
              </div>
            </div>
            
            {/* Time Slots Section */}
            <div>
              <h4 className="text-lg font-semibold text-gray-900 mb-4">Available Time Slots</h4>
              <div className="grid grid-cols-2 gap-3 max-h-80 overflow-y-auto custom-scrollbar">
                {timeSlots.map((slot) => {
                  const available = isSlotAvailable(slot);
                  const booked = isSlotBooked(slot);
                  const selected = selectedTime === slot;
                  
                  return (
                    <button
                      key={slot}
                      onClick={() => available && setSelectedTime(selected ? "" : slot)}
                      disabled={!available || booked}
                      className={`p-3 border rounded-lg text-center transition-colors ${
                        booked
                          ? "bg-gray-100 border-gray-300 text-gray-400 cursor-not-allowed"
                          : selected
                          ? "border-primary bg-primary text-white"
                          : available
                          ? "border-gray-300 hover:border-primary hover:bg-primary hover:text-white"
                          : "bg-gray-100 border-gray-300 text-gray-400 cursor-not-allowed"
                      }`}
                    >
                      <div className="font-medium">{slot}</div>
                      <div className="text-sm">
                        {booked ? "Booked" : selected ? "Selected" : available ? "Available" : "Unavailable"}
                      </div>
                    </button>
                  );
                })}
              </div>
              
              {/* Booking Summary */}
              <div className="mt-6 p-4 bg-gray-50 rounded-lg">
                <h5 className="font-semibold text-gray-900 mb-3">Booking Summary</h5>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span>Date:</span>
                    <span>{selectedDate.toLocaleDateString()}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Time:</span>
                    <span>
                      {selectedTime ? `${selectedTime} - ${(parseInt(selectedTime.split(':')[0]) + 1).toString().padStart(2, '0')}:00` : "Not selected"}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span>Duration:</span>
                    <span>1 hour</span>
                  </div>
                  <div className="flex justify-between font-semibold text-lg border-t pt-2 mt-2">
                    <span>Total:</span>
                    <span>₹{turf.pricePerHour}</span>
                  </div>
                </div>
              </div>
              
              <Button 
                onClick={handleProceedToPayment}
                disabled={!selectedTime || createBookingMutation.isPending}
                className="w-full mt-6 bg-primary hover:bg-primary/90"
              >
                {createBookingMutation.isPending ? "Creating Booking..." : "Proceed to Payment"}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* Payment Modal */}
      <PaymentModal
        isOpen={isPaymentOpen}
        onClose={() => {
          setIsPaymentOpen(false);
          onClose();
        }}
        bookingId={bookingId}
        amount={{
          base: totalAmount,
          platformFee,
          gst,
          total: finalAmount,
        }}
      />
    </>
  );
}
