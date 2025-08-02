import { useEffect, useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useToast } from "@/hooks/use-toast";
import { isUnauthorizedError } from "@/lib/authUtils";
import Navigation from "@/components/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";
import { 
  Building, 
  Calendar, 
  Users, 
  TrendingUp, 
  AlertTriangle, 
  Plus, 
  Edit, 
  Trash2,
  Check,
  X,
  Eye
} from "lucide-react";
import type { Turf, Booking, User } from "@shared/schema";

export default function AdminDashboard() {
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [isAddTurfOpen, setIsAddTurfOpen] = useState(false);
  const [editingTurf, setEditingTurf] = useState<Turf | null>(null);

  // Redirect if not admin
  useEffect(() => {
    if (!authLoading && (!isAuthenticated || user?.role !== 'admin')) {
      toast({
        title: "Access Denied",
        description: "Admin access required. Redirecting to login...",
        variant: "destructive",
      });
      setTimeout(() => {
        window.location.href = "/api/login";
      }, 500);
      return;
    }
  }, [isAuthenticated, authLoading, user, toast]);

  const { data: turfs = [] } = useQuery<Turf[]>({
    queryKey: ["/api/turfs"],
    enabled: isAuthenticated && user?.role === 'admin',
  });

  const { data: bookings = [] } = useQuery<(Booking & { turf: Turf; user: User })[]>({
    queryKey: ["/api/bookings"],
    enabled: isAuthenticated && user?.role === 'admin',
  });

  const createTurfMutation = useMutation({
    mutationFn: async (turfData: any) => {
      await apiRequest("POST", "/api/turfs", turfData);
    },
    onSuccess: () => {
      toast({
        title: "Turf Created",
        description: "New turf has been added successfully.",
      });
      queryClient.invalidateQueries({ queryKey: ["/api/turfs"] });
      setIsAddTurfOpen(false);
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
        description: "Failed to create turf. Please try again.",
        variant: "destructive",
      });
    },
  });

  const updateBookingMutation = useMutation({
    mutationFn: async ({ bookingId, status }: { bookingId: string; status: string }) => {
      await apiRequest("PUT", `/api/bookings/${bookingId}`, { status });
    },
    onSuccess: () => {
      toast({
        title: "Booking Updated",
        description: "Booking status has been updated successfully.",
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
        description: "Failed to update booking. Please try again.",
        variant: "destructive",
      });
    },
  });

  const deleteTurfMutation = useMutation({
    mutationFn: async (turfId: string) => {
      await apiRequest("DELETE", `/api/turfs/${turfId}`);
    },
    onSuccess: () => {
      toast({
        title: "Turf Deleted",
        description: "Turf has been removed successfully.",
      });
      queryClient.invalidateQueries({ queryKey: ["/api/turfs"] });
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
        description: "Failed to delete turf. Please try again.",
        variant: "destructive",
      });
    },
  });

  const todayBookings = bookings.filter(
    booking => new Date(booking.bookingDate).toDateString() === new Date().toDateString()
  );
  const pendingBookings = bookings.filter(booking => booking.status === 'pending');
  const monthlyRevenue = bookings
    .filter(booking => {
      const bookingMonth = new Date(booking.bookingDate).getMonth();
      const currentMonth = new Date().getMonth();
      return bookingMonth === currentMonth && booking.paymentStatus === 'completed';
    })
    .reduce((sum, booking) => sum + parseFloat(booking.totalAmount), 0);

  const stats = {
    totalTurfs: turfs.length,
    todayBookings: todayBookings.length,
    activeUsers: new Set(bookings.map(b => b.userId)).size,
    monthlyRevenue,
    pendingRequests: pendingBookings.length,
  };

  const handleCreateTurf = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    
    const turfData = {
      name: formData.get('name') as string,
      description: formData.get('description') as string,
      location: formData.get('location') as string,
      sportType: formData.get('sportType') as string,
      pricePerHour: formData.get('pricePerHour') as string,
      capacity: parseInt(formData.get('capacity') as string),
      amenities: (formData.get('amenities') as string).split(',').map(a => a.trim()),
      images: [],
    };

    createTurfMutation.mutate(turfData);
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
      
      {/* Header */}
      <section className="bg-gradient-to-r from-gray-800 to-gray-900 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-white">
            <h1 className="text-4xl font-bold mb-4">Admin Dashboard</h1>
            <p className="text-xl text-gray-300">Manage turfs, bookings, and users</p>
          </div>
        </div>
      </section>

      {/* Stats Cards */}
      <section className="py-8 -mt-16 relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-8">
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-indigo-500 rounded-lg">
                    <Building className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-indigo-600 text-sm font-medium">Total Turfs</p>
                    <p className="text-2xl font-bold text-indigo-900">{stats.totalTurfs}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-green-500 rounded-lg">
                    <Calendar className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-green-600 text-sm font-medium">Today's Bookings</p>
                    <p className="text-2xl font-bold text-green-900">{stats.todayBookings}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-blue-500 rounded-lg">
                    <Users className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-blue-600 text-sm font-medium">Active Users</p>
                    <p className="text-2xl font-bold text-blue-900">{stats.activeUsers}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-purple-500 rounded-lg">
                    <TrendingUp className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-purple-600 text-sm font-medium">Monthly Revenue</p>
                    <p className="text-2xl font-bold text-purple-900">₹{stats.monthlyRevenue.toLocaleString()}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card className="bg-white shadow-lg">
              <CardContent className="p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-red-500 rounded-lg">
                    <AlertTriangle className="w-6 h-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <p className="text-red-600 text-sm font-medium">Pending Requests</p>
                    <p className="text-2xl font-bold text-red-900">{stats.pendingRequests}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Management Sections */}
      <section className="pb-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Turf Management */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Turf Management</CardTitle>
                  <Dialog open={isAddTurfOpen} onOpenChange={setIsAddTurfOpen}>
                    <DialogTrigger asChild>
                      <Button className="bg-primary hover:bg-primary/90">
                        <Plus className="w-4 h-4 mr-2" />
                        Add Turf
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="max-w-md">
                      <DialogHeader>
                        <DialogTitle>Add New Turf</DialogTitle>
                      </DialogHeader>
                      <form onSubmit={handleCreateTurf} className="space-y-4">
                        <Input name="name" placeholder="Turf Name" required />
                        <Textarea name="description" placeholder="Description" />
                        <Input name="location" placeholder="Location" required />
                        <Select name="sportType" required>
                          <SelectTrigger>
                            <SelectValue placeholder="Sport Type" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="Football">Football</SelectItem>
                            <SelectItem value="Tennis">Tennis</SelectItem>
                            <SelectItem value="Basketball">Basketball</SelectItem>
                            <SelectItem value="Cricket">Cricket</SelectItem>
                            <SelectItem value="Badminton">Badminton</SelectItem>
                          </SelectContent>
                        </Select>
                        <Input name="pricePerHour" type="number" placeholder="Price per hour" required />
                        <Input name="capacity" type="number" placeholder="Capacity" required />
                        <Input name="amenities" placeholder="Amenities (comma separated)" />
                        <Button 
                          type="submit" 
                          className="w-full"
                          disabled={createTurfMutation.isPending}
                        >
                          {createTurfMutation.isPending ? "Creating..." : "Create Turf"}
                        </Button>
                      </form>
                    </DialogContent>
                  </Dialog>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 max-h-96 overflow-y-auto custom-scrollbar">
                  {turfs.map((turf) => (
                    <div key={turf.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div className="flex items-center space-x-3">
                        <div className="w-12 h-12 bg-gray-200 rounded-lg"></div>
                        <div>
                          <h4 className="font-medium text-gray-900">{turf.name}</h4>
                          <p className="text-sm text-gray-600">{turf.location} • ₹{turf.pricePerHour}/hr</p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Button variant="outline" size="sm">
                          <Edit className="w-4 h-4" />
                        </Button>
                        <Button 
                          variant="destructive" 
                          size="sm"
                          onClick={() => deleteTurfMutation.mutate(turf.id)}
                          disabled={deleteTurfMutation.isPending}
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* Recent Bookings */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Recent Bookings</CardTitle>
                  <Button variant="outline" size="sm">
                    View All
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 max-h-96 overflow-y-auto custom-scrollbar">
                  {bookings.slice(0, 10).map((booking) => (
                    <div key={booking.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div>
                        <h4 className="font-medium text-gray-900">
                          {booking.user.firstName} {booking.user.lastName}
                        </h4>
                        <p className="text-sm text-gray-600">
                          {booking.turf.name} • {new Date(booking.bookingDate).toLocaleDateString()} • {booking.startTime}
                        </p>
                        <div className="flex items-center space-x-2 mt-1">
                          <Badge className={getStatusColor(booking.status)}>
                            {booking.status.charAt(0).toUpperCase() + booking.status.slice(1)}
                          </Badge>
                          <span className="text-sm text-gray-500">₹{booking.totalAmount}</span>
                        </div>
                      </div>
                      {booking.status === 'pending' && (
                        <div className="flex items-center space-x-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => updateBookingMutation.mutate({ 
                              bookingId: booking.id, 
                              status: 'confirmed' 
                            })}
                            disabled={updateBookingMutation.isPending}
                          >
                            <Check className="w-4 h-4" />
                          </Button>
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => updateBookingMutation.mutate({ 
                              bookingId: booking.id, 
                              status: 'cancelled' 
                            })}
                            disabled={updateBookingMutation.isPending}
                          >
                            <X className="w-4 h-4" />
                          </Button>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>
    </div>
  );
}
