import { useAuth } from "@/hooks/useAuth";
import Navigation from "@/components/navigation";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useQuery } from "@tanstack/react-query";
import { Calendar, Clock, MapPin, Star, Users, TrendingUp } from "lucide-react";
import type { Turf, Booking } from "@shared/schema";

export default function Home() {
  const { user } = useAuth();

  const { data: turfs = [] } = useQuery<Turf[]>({
    queryKey: ["/api/turfs"],
  });

  const { data: bookings = [] } = useQuery<(Booking & { turf: Turf })[]>({
    queryKey: ["/api/bookings"],
  });

  const recentBookings = bookings.slice(0, 3);
  const featuredTurfs = turfs.slice(0, 6);

  const stats = {
    totalBookings: bookings.length,
    upcomingBookings: bookings.filter(b => b.status === 'confirmed' && new Date(b.bookingDate) > new Date()).length,
    totalSpent: bookings.reduce((sum, b) => sum + parseFloat(b.totalAmount), 0),
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      {/* Welcome Section */}
      <section className="gradient-primary py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center text-white">
            <h1 className="text-4xl font-bold mb-4">
              Welcome back, {user?.firstName || 'Player'}! 👋
            </h1>
            <p className="text-xl text-green-100 mb-8">
              Ready to book your next game?
            </p>
            <Button 
              size="lg" 
              className="bg-white text-primary hover:bg-gray-100"
              onClick={() => window.location.href = '/browse'}
            >
              Browse Turfs
            </Button>
          </div>
        </div>
      </section>

      {/* Stats Cards */}
      <section className="py-8 -mt-16 relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
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
                    <TrendingUp className="w-6 h-6 text-white" />
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

      {/* Recent Bookings */}
      {recentBookings.length > 0 && (
        <section className="py-8">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900">Recent Bookings</h2>
              <Button variant="outline" onClick={() => window.location.href = '/my-bookings'}>
                View All
              </Button>
            </div>
            
            <div className="grid grid-cols-1 gap-4">
              {recentBookings.map((booking) => (
                <Card key={booking.id} className="hover:shadow-md transition-shadow">
                  <CardContent className="p-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-4">
                        <div className="w-16 h-16 bg-gray-200 rounded-lg"></div>
                        <div>
                          <h4 className="font-semibold text-gray-900">{booking.turf.name}</h4>
                          <p className="text-sm text-gray-600">
                            {new Date(booking.bookingDate).toLocaleDateString()} • {booking.startTime} - {booking.endTime}
                          </p>
                          <div className="flex items-center space-x-2 mt-1">
                            <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                              booking.status === 'confirmed' 
                                ? 'bg-green-100 text-green-700' 
                                : booking.status === 'pending'
                                ? 'bg-yellow-100 text-yellow-700'
                                : 'bg-red-100 text-red-700'
                            }`}>
                              {booking.status.charAt(0).toUpperCase() + booking.status.slice(1)}
                            </span>
                            <span className="text-sm text-gray-500">₹{booking.totalAmount}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Featured Turfs */}
      <section className="py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Featured Turfs</h2>
            <Button variant="outline" onClick={() => window.location.href = '/browse'}>
              View All
            </Button>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {featuredTurfs.map((turf) => (
              <Card key={turf.id} className="overflow-hidden hover:shadow-lg transition-shadow hover-lift">
                <div className="h-48 bg-gray-200"></div>
                <CardContent className="p-4">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-lg font-semibold text-gray-900">{turf.name}</h3>
                    <div className="flex items-center">
                      <Star className="w-4 h-4 text-yellow-500 mr-1 fill-current" />
                      <span className="text-sm font-medium text-gray-700">{turf.rating}</span>
                    </div>
                  </div>
                  
                  <div className="flex items-center text-gray-600 mb-3">
                    <MapPin className="w-4 h-4 mr-2" />
                    <span>{turf.location}</span>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                      <span className="flex items-center">
                        <Users className="w-4 h-4 mr-1" />
                        {turf.capacity} players
                      </span>
                    </div>
                    <span className="text-lg font-bold text-gray-900">₹{turf.pricePerHour}/hr</span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
