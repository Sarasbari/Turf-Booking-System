import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MapPin, Star, Users, Clock } from "lucide-react";
import type { Turf } from "@shared/schema";

interface TurfCardProps {
  turf: Turf;
  onBook: () => void;
}

export default function TurfCard({ turf, onBook }: TurfCardProps) {
  return (
    <Card className="overflow-hidden hover:shadow-xl transition-shadow duration-300 hover-lift">
      <div className="relative h-48">
        {turf.images && turf.images.length > 0 ? (
          <img 
            src={turf.images[0]} 
            alt={turf.name}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-600">{turf.sportType}</div>
              <div className="text-sm text-gray-500">No image available</div>
            </div>
          </div>
        )}
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
            {turf.amenities && turf.amenities.slice(0, 2).map((amenity, index) => (
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
            onClick={onBook}
            className="bg-primary hover:bg-primary/90"
          >
            Book Now
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
