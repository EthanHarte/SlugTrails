//
//  MapViewController.m
//  SlugTrails
//
//  Created by Andrew Lee on 1/9/15.
//  Copyright (c) 2015 Andrew Lee. All rights reserved.
//


#import "FastServerSocket.h"
#import "FastSocket.h"
#include <unistd.h>
#include <netdb.h>
#include <sys/socket.h>


#import "MapViewController.h"
#import <GoogleMaps/GoogleMaps.h>

@interface MapViewController ()

@end

@implementation MapViewController 
{
    GMSMapView *mapView_;
    CLLocationManager *locManager_;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    locManager_ = [[CLLocationManager alloc]init];
    locManager_.delegate = self;
    if ([locManager_ respondsToSelector:@selector(requestWhenInUseAuthorization)]) {
        [locManager_ requestWhenInUseAuthorization];
    }
    [locManager_ startUpdatingLocation];
    
    // Create a GMSCameraPosition that tells the map to display the
    // coordinate -33.86,151.20 at zoom level 6.
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:-33.86
                                                            longitude:151.20
                                                                 zoom:6];
    mapView_ = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    mapView_.myLocationEnabled = YES;
    self.view = mapView_;
    NSLog(@"User's location: %@", mapView_.myLocation);
    // Creates a marker in the center of the map.
    GMSMarker *marker = [[GMSMarker alloc] init];
    //CLLocationCoordinate2D myLocation = self.mapView_.myLocation.coordinate;
    //marker.position = CLLocationCoordinate2DMake();
    marker.position = CLLocationCoordinate2DMake(-33.86, 151.20);
    //marker.position = CLLocationCoordinate2DMake(mapView_.myLocation.coordinate.la);
    marker.title = @"Sydney";
    marker.snippet = @"Australia";
    marker.map = mapView_;
    
//    locManager_ = [[CLLocationManager alloc] init];
//    locManager_.delegate = self;
//    [locManager_ startUpdatingLocation];
}

// Implementation of CLLocationManagerDelegate method to receive the GPS current location update

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
    // Examine the data in locations which should pass you information about lat/long
    NSLog(@"%@", locations);
    NSLog(@"User's location: %@", mapView_.myLocation);
    
    // Update the map and drop pins based on lat/long
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
