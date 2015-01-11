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
//@property (strong, nonatomic) NSMutableArray *array;
//@property (nonatomic, strong) NSMutableArray *Subtext;
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
    
//    
//    NSData *data = [@"getallm_2.2_3.8_5.2\r" dataUsingEncoding:NSUTF8StringEncoding];
//    
//    long count = [clients sendBytes:[data bytes] count:[data length]];
//    
//    long expectedLength=64000;
//    
//    
//    NSString *received=@"";
//    char bytes[expectedLength];
//    //while (![received isEqual:NULL]) {
//    
//    received = @"";
//    [clients receiveBytes:bytes limit:expectedLength];
//    //[clients buffer:bytes size:expectedLength];
//    NSLog(@"%s", bytes);
//    
//    received = [[NSString alloc] initWithBytes:bytes length:strlen(bytes) encoding:NSUTF8StringEncoding];
//    NSLog(@"%@",received);
//    
//    
//    
//    NSMutableArray *INPUT=[NSMutableArray array];
//    [INPUT addObject:[NSString stringWithFormat: @"%@",received]];
//    NSLog(@"myArray:\n%@", INPUT);
//    
//    // The strings are divided into several different strings now
//    NSArray *fields = INPUT;
//    fields = [received componentsSeparatedByString:@"&"];
//    
//    NSArray *fins;
//    NSMutableArray *title = [[NSMutableArray alloc] init];
//    NSMutableArray *sub = [[NSMutableArray alloc] init];
//    
//    for (int i = 0; i < [fields count]; i++){
//        //...do something useful with myArrayElement
//        fins=[fields[i] componentsSeparatedByString:@"_"];
//        for (int j=0; j<[fins count]; j++){
//            if(j==1) {
//                NSLog(fins[j]);
//                [title addObject: fins[j]];
//            }
//            else if(j==5){
//                [sub addObject:fins[j]];
//            }
//        }
//    }
//    _array = title;
//    _Subtext = sub;
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
