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
@property (strong, nonatomic) NSMutableArray *array;
@property (nonatomic, strong) NSMutableArray *Subtext;
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
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:36.0000
                                                            longitude:-122.0600
                                                                 zoom:6];
    mapView_ = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    mapView_.myLocationEnabled = YES;
    self.view = mapView_;
    NSLog(@"User's location: %@", mapView_.myLocation);
    // Creates a marker in the center of the map.
//    GMSMarker *marker = [[GMSMarker alloc] init];
//    //CLLocationCoordinate2D myLocation = self.mapView_.myLocation.coordinate;
//    //marker.position = CLLocationCoordinate2DMake();
//    marker.position = CLLocationCoordinate2DMake(36.0000, -122.0600);
//    //marker.position = CLLocationCoordinate2DMake(mapView_.myLocation.coordinate.la);
//    marker.title = @"Lion";
//    marker.snippet = @"California";
//    marker.map = mapView_;
//    
    
    FastSocket *clients = [[FastSocket alloc] initWithHost:@"hackucsc2015.no-ip.info" andPort:@"25565"];
    [clients connect];
    NSData *data = [@"getallm_2.2_3.8_5.2\r" dataUsingEncoding:NSUTF8StringEncoding];
    long count = [clients sendBytes:[data bytes] count:[data length]];

    long expectedLength=64000;
    
    
    NSString *received=@"";
    char bytes[expectedLength];
    //while (![received isEqual:NULL]) {
    
    received = @"";
    [clients receiveBytes:bytes limit:expectedLength];
    //[clients buffer:bytes size:expectedLength];
    NSLog(@"%s", bytes);
    
    received = [[NSString alloc] initWithBytes:bytes length:strlen(bytes) encoding:NSUTF8StringEncoding];
    NSLog(@"%@",received);
    
    
    
    NSMutableArray *INPUT=[NSMutableArray array];
    [INPUT addObject:[NSString stringWithFormat: @"%@",received]];
    NSLog(@"myArray:\n%@", INPUT);
    
    // The strings are divided into several different strings now
    NSArray *fields = INPUT;
    fields = [received componentsSeparatedByString:@"&"];
    
    NSArray *fins;
    NSMutableArray *name = [[NSMutableArray alloc] init];
    NSMutableArray *description = [[NSMutableArray alloc] init];
    NSMutableArray *latitude = [[NSMutableArray alloc] init];
    NSMutableArray *longitude = [[NSMutableArray alloc] init];
    
    for (int i = 0; i < [fields count]; i++){
        //...do something useful with myArrayElement
        fins=[fields[i] componentsSeparatedByString:@"_"];
        for (int j=0; j<[fins count]; j++){
            if(j==1) {
                //                NSLog(@"latitude:%@",fins[j]);
                [name addObject: fins[j]];
            }
            else if(j==3) {
//                NSLog(@"latitude:%@",fins[j]);
                [latitude addObject: fins[j]];
            }
            else if(j==4){
//                NSLog(@"longitude:%@",fins[j]);
                [longitude addObject:fins[j]];
            }
            else if(j==5){
                [description addObject:fins[j]];
            }
        }
    }
    
    
    
    for (int i = 0; i < [latitude count]; i++){
        //...do something useful with myArrayElement
        float lat = [latitude[i] floatValue];
        float lon = [longitude[i] floatValue];

        GMSMarker *marker = [[GMSMarker alloc] init];
        //CLLocationCoordinate2D myLocation = self.mapView_.myLocation.coordinate;
        //marker.position = CLLocationCoordinate2DMake();
        marker.position = CLLocationCoordinate2DMake(lat, lon);
        //marker.position = CLLocationCoordinate2DMake(mapView_.myLocation.coordinate.la);
        marker.title = name[i];
        marker.snippet = description[i];
        marker.map = mapView_;
    
        
//        marker.position = CLLocationCoordinate2DMake(lat, lon);
//        marker.title=toLocationFromResultVC; marker.snippet=@"Destination";
//        marker.map = mapView_;
        
//        GMSMarker *marker2 = [[GMSMarker alloc] init];
//        marker2.position=CLLocationCoordinate2DMake(fromLatitudeDouble, fromLongitudeDouble);
//        marker2.title=fromLocationFromResultVC; marker.snippet=@"Source";
//        marker2.map = mapView_;
//        
        
        
    }
    _array = latitude;
    _Subtext = longitude;
//
    
    
    
    
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
