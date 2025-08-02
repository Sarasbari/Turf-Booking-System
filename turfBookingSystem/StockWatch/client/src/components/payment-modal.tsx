import { useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useToast } from "@/hooks/use-toast";
import { isUnauthorizedError } from "@/lib/authUtils";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";
import { Lock, CreditCard, Smartphone, Building, X } from "lucide-react";

interface PaymentModalProps {
  isOpen: boolean;
  onClose: () => void;
  bookingId: string;
  amount: {
    base: number;
    platformFee: number;
    gst: number;
    total: number;
  };
}

export default function PaymentModal({ isOpen, onClose, bookingId, amount }: PaymentModalProps) {
  const { user } = useAuth();
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [paymentMethod, setPaymentMethod] = useState("card");
  const [cardDetails, setCardDetails] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardholderName: "",
  });

  const processPaymentMutation = useMutation({
    mutationFn: async (paymentData: any) => {
      const response = await apiRequest("POST", "/api/payments", paymentData);
      return response.json();
    },
    onSuccess: () => {
      toast({
        title: "Payment Successful! 🎉",
        description: "Your booking has been confirmed. You'll receive a confirmation email shortly.",
      });
      queryClient.invalidateQueries({ queryKey: ["/api/bookings"] });
      onClose();
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
        title: "Payment Failed",
        description: "Failed to process payment. Please try again.",
        variant: "destructive",
      });
    },
  });

  const handlePayment = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (paymentMethod === "card") {
      // Validate card details
      if (!cardDetails.cardNumber || !cardDetails.expiryDate || !cardDetails.cvv || !cardDetails.cardholderName) {
        toast({
          title: "Incomplete Details",
          description: "Please fill in all card details.",
          variant: "destructive",
        });
        return;
      }
    }

    const paymentData = {
      bookingId,
      amount: amount.base.toString(),
      platformFee: amount.platformFee.toString(),
      gst: amount.gst.toString(),
      totalAmount: amount.total.toString(),
      paymentMethod,
    };

    processPaymentMutation.mutate(paymentData);
  };

  const handleCardInputChange = (field: string, value: string) => {
    let formattedValue = value;
    
    // Format card number with spaces
    if (field === "cardNumber") {
      formattedValue = value.replace(/\s/g, "").replace(/(.{4})/g, "$1 ").trim();
      if (formattedValue.length > 19) return; // Limit to 16 digits + 3 spaces
    }
    
    // Format expiry date
    if (field === "expiryDate") {
      formattedValue = value.replace(/\D/g, "").replace(/(\d{2})(\d)/, "$1/$2");
      if (formattedValue.length > 5) return; // Limit to MM/YY format
    }
    
    // Format CVV
    if (field === "cvv") {
      formattedValue = value.replace(/\D/g, "");
      if (formattedValue.length > 3) return; // Limit to 3 digits
    }

    setCardDetails(prev => ({ ...prev, [field]: formattedValue }));
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <DialogTitle className="text-xl font-bold">Payment Details</DialogTitle>
            <Button variant="ghost" size="sm" onClick={onClose}>
              <X className="w-5 h-5" />
            </Button>
          </div>
        </DialogHeader>
        
        <div className="space-y-6">
          {/* Payment Summary */}
          <div className="bg-gray-50 rounded-lg p-4">
            <h4 className="font-semibold text-gray-900 mb-3">Booking Summary</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span>Base Amount</span>
                <span>₹{amount.base}</span>
              </div>
              <div className="flex justify-between">
                <span>Platform Fee</span>
                <span>₹{amount.platformFee}</span>
              </div>
              <div className="flex justify-between">
                <span>GST (18%)</span>
                <span>₹{amount.gst}</span>
              </div>
              <div className="border-t pt-2 mt-2 font-semibold">
                <div className="flex justify-between">
                  <span>Total Amount</span>
                  <span>₹{amount.total}</span>
                </div>
              </div>
            </div>
          </div>
          
          {/* Payment Methods */}
          <div>
            <h4 className="font-semibold text-gray-900 mb-4">Payment Method</h4>
            <RadioGroup value={paymentMethod} onValueChange={setPaymentMethod}>
              <div className="flex items-center space-x-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:border-primary">
                <RadioGroupItem value="card" id="card" />
                <Label htmlFor="card" className="flex items-center justify-between w-full cursor-pointer">
                  <span className="font-medium">Credit/Debit Card</span>
                  <div className="flex space-x-2">
                    <CreditCard className="w-5 h-5 text-blue-600" />
                  </div>
                </Label>
              </div>
              
              <div className="flex items-center space-x-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:border-primary">
                <RadioGroupItem value="upi" id="upi" />
                <Label htmlFor="upi" className="flex items-center justify-between w-full cursor-pointer">
                  <span className="font-medium">UPI Payment</span>
                  <Smartphone className="w-5 h-5 text-purple-600" />
                </Label>
              </div>
              
              <div className="flex items-center space-x-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:border-primary">
                <RadioGroupItem value="netbanking" id="netbanking" />
                <Label htmlFor="netbanking" className="flex items-center justify-between w-full cursor-pointer">
                  <span className="font-medium">Net Banking</span>
                  <Building className="w-5 h-5 text-green-600" />
                </Label>
              </div>
            </RadioGroup>
          </div>
          
          {/* Card Details Form */}
          {paymentMethod === "card" && (
            <form onSubmit={handlePayment} className="space-y-4">
              <div>
                <Label htmlFor="cardNumber" className="text-sm font-medium text-gray-700">
                  Card Number
                </Label>
                <Input
                  id="cardNumber"
                  placeholder="1234 5678 9012 3456"
                  value={cardDetails.cardNumber}
                  onChange={(e) => handleCardInputChange("cardNumber", e.target.value)}
                  className="mt-1"
                />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="expiryDate" className="text-sm font-medium text-gray-700">
                    Expiry Date
                  </Label>
                  <Input
                    id="expiryDate"
                    placeholder="MM/YY"
                    value={cardDetails.expiryDate}
                    onChange={(e) => handleCardInputChange("expiryDate", e.target.value)}
                    className="mt-1"
                  />
                </div>
                <div>
                  <Label htmlFor="cvv" className="text-sm font-medium text-gray-700">
                    CVV
                  </Label>
                  <Input
                    id="cvv"
                    placeholder="123"
                    type="password"
                    value={cardDetails.cvv}
                    onChange={(e) => handleCardInputChange("cvv", e.target.value)}
                    className="mt-1"
                  />
                </div>
              </div>
              
              <div>
                <Label htmlFor="cardholderName" className="text-sm font-medium text-gray-700">
                  Cardholder Name
                </Label>
                <Input
                  id="cardholderName"
                  placeholder="John Doe"
                  value={cardDetails.cardholderName}
                  onChange={(e) => setCardDetails(prev => ({ ...prev, cardholderName: e.target.value }))}
                  className="mt-1"
                />
              </div>
            </form>
          )}
          
          {/* UPI Payment */}
          {paymentMethod === "upi" && (
            <div className="text-center py-4">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Smartphone className="w-8 h-8 text-purple-600" />
              </div>
              <p className="text-gray-600">You will be redirected to your UPI app to complete the payment.</p>
            </div>
          )}
          
          {/* Net Banking */}
          {paymentMethod === "netbanking" && (
            <div className="text-center py-4">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Building className="w-8 h-8 text-green-600" />
              </div>
              <p className="text-gray-600">You will be redirected to your bank's website to complete the payment.</p>
            </div>
          )}
          
          <Button 
            onClick={paymentMethod === "card" ? undefined : handlePayment}
            type={paymentMethod === "card" ? "submit" : "button"}
            form={paymentMethod === "card" ? undefined : undefined}
            className="w-full bg-primary hover:bg-primary/90"
            disabled={processPaymentMutation.isPending}
          >
            <Lock className="w-4 h-4 mr-2" />
            {processPaymentMutation.isPending ? "Processing..." : `Pay ₹${amount.total}`}
          </Button>
          
          <p className="text-xs text-gray-500 text-center flex items-center justify-center">
            <Lock className="w-3 h-3 mr-1" />
            Your payment information is secure and encrypted
          </p>
        </div>
      </DialogContent>
    </Dialog>
  );
}
