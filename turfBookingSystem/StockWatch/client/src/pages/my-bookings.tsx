import { useEffect } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useToast } from "@/hooks/use-toast";
import { isUnauthorizedError } from "@/lib/authUtils";
import Navigation from "@/components/navigation";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";
import { Calendar, Clock, MapPin, X, Eye } from "lucide-react";
import type { Booking, Turf, User } from "@shared/schema";

export default function MyBookings() {
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();
  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
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
  }, [isAuthenticated, authLoading, toast]);

  const { data: bookings = [], isLoading } = useQuery<(Booking & { turf: Turf; user: User })[]>({
    queryKey: ["/api/bookings"],
    enabled: isAuthenticated,
  });

  const cancelBookingMutation = useMutation({
    mutationFn: async (bookingId: string) => {
      await apiRequest("DELETE", `/api/bookings/${bookingId}`);
    },
    onSuccess: () => {
      toast({
        title: "Booking Cancelled",
        description: "Your booking has been cancelled successfully.",
      });
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
        title: "Error",
        description: "Failed to cancel booking. Please try again.",
        variant: "destructive",
      });
    },
  });

  const upcomingBookings = bookings.filter(
    booking => booking.status === 'confirmed' && new Date(booking.bookingDate) > new Date()
  );
  const pastBookings = bookings.filter(
    booking => new Date(booking.bookingDate) <= new Date() || booking.status === 'cancelled'
  );

  const stats = {
    totalBookings: bookings.length,
    upcomingBookings: upcomingBookings.length,
    totalSpent: bookings.reduce((sum, b) => sum + parseFloat(b.totalAmount), 0),
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'confirmed':
        return 'bg-green-100 text-green-700';
      case 'pending':
        return 'bg-yellow-100 text-yellow-700';
      case 'cancelled':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      {/* Header Section */}
      <section className="gradient-primary py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-white">
            <h1 className="text-4xl font-bold mb-4">My Bookings</h1>
            <p className="text-xl text-green-100">Manage your turf reservations</p>
          </div>
        </div>
      </section>

      {/* Stats Cards */}
      <section className="py-8 -mt-16 relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-blue-500 rounded-lg">
                    <Calendar className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-blue-600 text-sm font-medium">Total Bookings</p>
                    <p className="text-2xl font-bold text-blue-900">{stats.totalBookings}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-green-500 rounded-lg">
                    <Clock className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-green-600 text-sm font-medium">Upcoming</p>
                    <p className="text-2xl font-bold text-green-900">{stats.upcomingBookings}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-orange-500 rounded-lg">
                    <Calendar className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-orange-600 text-sm font-medium">Total Spent</p>
                    <p className="text-2xl font-bold text-orange-900">₹{stats.totalSpent.toLocaleString()}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Bookings Sections */}
      <section className="pb-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Upcoming Bookings */}
          {upcomingBookings.length > 0 && (
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Upcoming Bookings</h2>
              <div className="grid grid-cols-1 gap-4">
                {upcomingBookings.map((booking) => (
                  <Card key={booking.id} className="hover:shadow-md transition-shadow">
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                          <div className="w-16 h-16 bg-gray-200 rounded-lg flex items-center justify-center">
                            <Calendar className="w-6 h-6 text-gray-400" />
                          </div>
                          <div>
                            <h4 className="font-semibold text-gray-900">{booking.turf.name}</h4>
                            <div className="flex items-center text-gray-600 mt-1">
                              <MapPin className="w-4 h-4 mr-1" />
                              <span>{booking.turf.location}</span>
                            </div>
                            <p className="text-sm text-gray-600 mt-1">
                              {new Date(booking.bookingDate).toLocaleDateString()} • {booking.startTime} - {booking.endTime}
                            </p>
                            <div className="flex items-center space-x-2 mt-2">
                              <Badge className={getStatusColor(booking.status)}>
                                {booking.status.charAt(0).toUpperCase() + booking.status.slice(1)}
                              </Badge>
                              <span className="text-sm font-medium text-gray-900">₹{booking.totalAmount}</span>
                            </div>
                          </div>
                        </div>
                        <div className="flex items-center space-x-2">
                          <Button variant="outline" size="sm">
                            <Eye className="w-4 h-4 mr-2" />
                            View Details
                          </Button>
                          {booking.status === 'confirmed' && (
                            <Button
                              variant="destructive"
                              size="sm"
                              onClick={() => cancelBookingMutation.mutate(booking.id)}
                              disabled={cancelBookingMutation.isPending}
                            >
                              <X className="w-4 h-4 mr-2" />
                              Cancel
                            </Button>
                          )}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          )}

          {/* Past Bookings */}
          {pastBookings.length > 0 && (
            <div>
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Past Bookings</h2>
              <div className="grid grid-cols-1 gap-4">
                {pastBookings.map((booking) => (
                  <Card key={booking.id} className="opacity-75">
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                          <div className="w-16 h-16 bg-gray-200 rounded-lg flex items-center justify-center">
                            <Calendar className="w-6 h-6 text-gray-400" />
                          </div>
                          <div>
                            <h4 className="font-semibold text-gray-900">{booking.turf.name}</h4>
                            <div className="flex items-center text-gray-600 mt-1">
                              <MapPin className="w-4 h-4 mr-1" />
                              <span>{booking.turf.location}</span>
                            </div>
                            <p className="text-sm text-gray-600 mt-1">
                              {new Date(booking.bookingDate).toLocaleDateString()} • {booking.startTime} - {booking.endTime}
                            </p>
                            <div className="flex items-center space-x-2 mt-2">
                              <Badge className={getStatusColor(booking.status)}>
                                {booking.status.charAt(0).toUpperCase() + booking.status.slice(1)}
                              </Badge>
                              <span className="text-sm font-medium text-gray-900">₹{booking.totalAmount}</span>
                            </div>
                          </div>
                        </div>
                        <div className="flex items-center space-x-2">
                          <Button variant="outline" size="sm">
                            <Eye className="w-4 h-4 mr-2" />
                            View Details
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          )}

          {/* Empty State */}
          {bookings.length === 0 && !isLoading && (
            <div className="text-center py-12">
              <div className="w-24 h-24 bg-gray-200 rounded-full mx-auto mb-4 flex items-center justify-center">
                <Calendar className="w-8 h-8 text-gray-400" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">No bookings yet</h3>
              <p className="text-gray-600 mb-4">Start by booking your first turf</p>
              <Button onClick={() => window.location.href = '/browse'}>
                Browse Turfs
              </Button>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
