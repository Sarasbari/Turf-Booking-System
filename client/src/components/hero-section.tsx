import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { MapPin, Calendar, Search } from "lucide-react";

export default function HeroSection() {
  const [searchData, setSearchData] = useState({
    location: "",
    sportType: "",
    date: "",
  });

  const handleSearch = () => {
    const params = new URLSearchParams();
    if (searchData.location) params.append("location", searchData.location);
    if (searchData.sportType) params.append("sportType", searchData.sportType);
    if (searchData.date) params.append("date", searchData.date);
    
    window.location.href = `/browse?${params.toString()}`;
  };

  return (
    <section className="relative gradient-primary py-20">
      <div className="absolute inset-0 bg-black bg-opacity-20"></div>
      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-4xl md:text-6xl font-bold text-white mb-6">
          Book Your Perfect Turf
        </h1>
        <p className="text-xl text-green-100 mb-8 max-w-2xl mx-auto">
          Discover and book premium sports facilities in your area. From football pitches to tennis courts.
        </p>
        
        {/* Search Component */}
        <Card className="shadow-2xl max-w-4xl mx-auto">
          <CardContent className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
                <div className="relative">
                  <MapPin className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                  <Input
                    placeholder="City or area"
                    value={searchData.location}
                    onChange={(e) => setSearchData(prev => ({ ...prev, location: e.target.value }))}
                    className="pl-10"
                  />
                </div>
              </div>
              
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">Sport Type</label>
                <Select
                  value={searchData.sportType}
                  onValueChange={(value) => setSearchData(prev => ({ ...prev, sportType: value }))}
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
              
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                  <Input
                    type="date"
                    value={searchData.date}
                    onChange={(e) => setSearchData(prev => ({ ...prev, date: e.target.value }))}
                    min={new Date().toISOString().split('T')[0]}
                    className="pl-10"
                  />
                </div>
              </div>
              
              <div className="flex items-end">
                <Button 
                  onClick={handleSearch}
                  className="w-full bg-primary hover:bg-primary/90 shadow-lg"
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
  );
}
