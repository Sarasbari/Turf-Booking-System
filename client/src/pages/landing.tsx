import Navigation from "@/components/navigation";
import HeroSection from "@/components/hero-section";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MapPin, Star, Users, Clock } from "lucide-react";

const featuredTurfs = [
  {
    id: "1",
    name: "Elite Sports Arena",
    location: "Downtown Sports Complex",
    sportType: "Football",
    pricePerHour: "800",
    rating: "4.8",
    capacity: 10,
    images: ["https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&h=600"],
    amenities: ["5v5", "Artificial Grass"]
  },
  {
    id: "2",
    name: "Ace Tennis Courts",
    location: "Riverside Club",
    sportType: "Tennis",
    pricePerHour: "600",
    rating: "4.9",
    capacity: 4,
    images: ["https://images.unsplash.com/photo-1554068865-24cecd4e34b8?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&h=600"],
    amenities: ["Clay Court", "Professional"]
  },
  {
    id: "3",
    name: "Prime Basketball Arena",
    location: "Central Sports Hub",
    sportType: "Basketball",
    pricePerHour: "1200",
    rating: "4.7",
    capacity: 10,
    images: ["https://images.unsplash.com/photo-1574623452334-1e0ac2b3ccb4?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&h=600"],
    amenities: ["Indoor", "Wooden Flooring"]
  }
];

export default function Landing() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      <HeroSection />
      
      {/* Featured Turfs Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">Featured Turfs</h2>
            <p className="text-lg text-gray-600">Discover premium sports facilities near you</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredTurfs.map((turf) => (
              <Card key={turf.id} className="overflow-hidden hover:shadow-xl transition-shadow duration-300 hover-lift">
                <div className="relative h-48">
                  <img 
                    src={turf.images[0]} 
                    alt={turf.name}
                    className="w-full h-full object-cover"
                  />
                </div>
                <CardContent className="p-6">
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="text-xl font-semibold text-gray-900">{turf.name}</h3>
                    <div className="flex items-center">
                      <Star className="w-4 h-4 text-yellow-500 mr-1 fill-current" />
                      <span className="text-sm font-medium text-gray-700">{turf.rating}</span>
                    </div>
                  </div>
                  
                  <div className="flex items-center text-gray-600 mb-3">
                    <MapPin className="w-4 h-4 mr-2" />
                    <span>{turf.location}</span>
                  </div>
                  
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex space-x-2">
                      <span className="px-2 py-1 bg-primary/10 text-primary text-xs font-medium rounded-full">
                        {turf.sportType}
                      </span>
                      {turf.amenities.map((amenity, index) => (
                        <span key={index} className="px-2 py-1 bg-blue-100 text-blue-700 text-xs font-medium rounded-full">
                          {amenity}
                        </span>
                      ))}
                    </div>
                    <span className="text-lg font-bold text-gray-900">₹{turf.pricePerHour}/hr</span>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                      <span className="flex items-center">
                        <Users className="w-4 h-4 mr-1" />
                        {turf.capacity} players
                      </span>
                      <span className="flex items-center">
                        <Clock className="w-4 h-4 mr-1" />
                        Available
                      </span>
                    </div>
                    <Button 
                      className="bg-primary hover:bg-primary/90"
                      onClick={() => window.location.href = '/api/login'}
                    >
                      Login to Book
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="text-center mt-12">
            <Button 
              variant="outline" 
              size="lg"
              onClick={() => window.location.href = '/api/login'}
            >
              Login to View All Turfs
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">Why Choose TurfBook?</h2>
            <p className="text-lg text-gray-600">Experience the best in sports facility booking</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                <MapPin className="w-8 h-8 text-primary" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Premium Locations</h3>
              <p className="text-gray-600">Access to the best sports facilities in prime locations across the city</p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-secondary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                <Clock className="w-8 h-8 text-secondary" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Instant Booking</h3>
              <p className="text-gray-600">Book your favorite turf instantly with real-time availability</p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-accent/10 rounded-full flex items-center justify-center mx-auto mb-4">
                <Star className="w-8 h-8 text-accent" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Quality Assured</h3>
              <p className="text-gray-600">All facilities are verified and maintained to the highest standards</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <h3 className="text-2xl font-bold text-primary mb-4">TurfBook</h3>
              <p className="text-gray-300 mb-4">Your premier destination for booking sports facilities. Play more, worry less.</p>
            </div>
            
            <div>
              <h4 className="text-lg font-semibold mb-4">Quick Links</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">Browse Turfs</a></li>
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">How It Works</a></li>
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">Pricing</a></li>
              </ul>
            </div>
            
            <div>
              <h4 className="text-lg font-semibold mb-4">Support</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">Help Center</a></li>
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">Contact Us</a></li>
                <li><a href="#" className="text-gray-300 hover:text-primary transition-colors">Terms of Service</a></li>
              </ul>
            </div>
            
            <div>
              <h4 className="text-lg font-semibold mb-4">Contact Info</h4>
              <div className="space-y-2 text-gray-300">
                <p>+91 98765 43210</p>
                <p>support@turfbook.com</p>
                <p>Mumbai, Maharashtra</p>
              </div>
            </div>
          </div>
          
          <div className="border-t border-gray-800 mt-8 pt-8 text-center">
            <p className="text-gray-400">&copy; 2024 TurfBook. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
