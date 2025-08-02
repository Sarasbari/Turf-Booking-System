import { useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import Navigation from "@/components/navigation";
import TurfCard from "@/components/turf-card";
import BookingModal from "@/components/booking-modal";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useQuery } from "@tanstack/react-query";
import { Search, Filter } from "lucide-react";
import type { Turf } from "@shared/schema";

export default function BrowseTurfs() {
  const { user } = useAuth();
  const [searchFilters, setSearchFilters] = useState({
    location: "",
    sportType: "",
    date: "",
  });
  const [selectedTurf, setSelectedTurf] = useState<Turf | null>(null);

  const { data: turfs = [], isLoading } = useQuery<Turf[]>({
    queryKey: ["/api/turfs", searchFilters],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (searchFilters.location) params.append("location", searchFilters.location);
      if (searchFilters.sportType) params.append("sportType", searchFilters.sportType);
      if (searchFilters.date) params.append("date", searchFilters.date);
      
      const response = await fetch(`/api/turfs?${params}`);
      if (!response.ok) throw new Error("Failed to fetch turfs");
      return response.json();
    },
  });

  const handleSearch = () => {
    // Trigger refetch with new filters
    window.location.reload();
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      {/* Search Section */}
      <section className="gradient-primary py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-8">
            <h1 className="text-4xl font-bold text-white mb-4">Find Your Perfect Turf</h1>
            <p className="text-xl text-green-100">Browse and book premium sports facilities</p>
          </div>
          
          <Card className="shadow-2xl">
            <CardContent className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
                  <Input
                    placeholder="City or area"
                    value={searchFilters.location}
                    onChange={(e) => setSearchFilters(prev => ({ ...prev, location: e.target.value }))}
                    className="w-full"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Sport Type</label>
                  <Select
                    value={searchFilters.sportType}
                    onValueChange={(value) => setSearchFilters(prev => ({ ...prev, sportType: value }))}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select sport" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Football">Football</SelectItem>
                      <SelectItem value="Tennis">Tennis</SelectItem>
                      <SelectItem value="Basketball">Basketball</SelectItem>
                      <SelectItem value="Cricket">Cricket</SelectItem>
                      <SelectItem value="Badminton">Badminton</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
                  <Input
                    type="date"
                    value={searchFilters.date}
                    onChange={(e) => setSearchFilters(prev => ({ ...prev, date: e.target.value }))}
                    min={new Date().toISOString().split('T')[0]}
                    className="w-full"
                  />
                </div>
                
                <div className="flex items-end">
                  <Button 
                    onClick={handleSearch}
                    className="w-full bg-primary hover:bg-primary/90"
                  >
                    <Search className="w-4 h-4 mr-2" />
                    Search
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Turfs Grid */}
      <section className="py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-8">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">Available Turfs</h2>
              <p className="text-gray-600">{turfs.length} turfs found</p>
            </div>
            
            <div className="flex items-center space-x-4">
              <Button variant="outline" size="sm">
                <Filter className="w-4 h-4 mr-2" />
                Filters
              </Button>
            </div>
          </div>

          {isLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <Card key={i} className="overflow-hidden">
                  <div className="h-48 bg-gray-200 animate-pulse"></div>
                  <CardContent className="p-4">
                    <div className="space-y-3">
                      <div className="h-4 bg-gray-200 rounded animate-pulse"></div>
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4"></div>
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-1/2"></div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : turfs.length === 0 ? (
            <div className="text-center py-12">
              <div className="w-24 h-24 bg-gray-200 rounded-full mx-auto mb-4 flex items-center justify-center">
                <Search className="w-8 h-8 text-gray-400" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">No turfs found</h3>
              <p className="text-gray-600 mb-4">Try adjusting your search filters</p>
              <Button 
                variant="outline"
                onClick={() => setSearchFilters({ location: "", sportType: "", date: "" })}
              >
                Clear Filters
              </Button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {turfs.map((turf) => (
                <TurfCard 
                  key={turf.id} 
                  turf={turf}
                  onBook={() => setSelectedTurf(turf)}
                />
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Booking Modal */}
      {selectedTurf && (
        <BookingModal
          turf={selectedTurf}
          isOpen={!!selectedTurf}
          onClose={() => setSelectedTurf(null)}
        />
      )}
    </div>
  );
}
