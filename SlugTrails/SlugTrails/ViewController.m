//
//  ViewController.m
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





#import "ViewController.h"
#import <GoogleMaps/GoogleMaps.h>

@interface ViewController ()

@end

@implementation ViewController
{
    GMSMapView *mapView_;
    CLLocationManager *locManager_;

}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    }

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.Animal resignFirstResponder];
    [self.Comment resignFirstResponder];

}

- (IBAction)TagClick:(id)sender {
    //NSLog(@"IT WORKS");
    
    //self.Animal.text=self.customTextField.text;
    
    //NSString *chosen=self.Animal.text;
    
    if([self.Animal.text length]>0 && [self.Comment.text length]>0){
    
        
    mapView_.myLocationEnabled = YES;
    self.view = mapView_;
    NSLog(@"user:%@",mapView_.myLocation);
    NSString *chosen=[self.Animal.text stringByAppendingString:@"\r"];
    NSString *said=self.Comment.text;
    ///////////
    
        
    
    NSString * latitude= [NSString stringWithFormat:@"%f",37.000078];
    NSString * longitude = [NSString stringWithFormat:@"%f",-122.053825];
    
    
    int r = arc4random_uniform(1000000);
    NSString * random = [NSString stringWithFormat:@"%d",r];
    
    
    NSString* str = @"post_";
    str = [str stringByAppendingString:random];
    str = [str stringByAppendingString: @"_"];
    str = [str stringByAppendingString:said];
    str = [str stringByAppendingString:@"_0_"];
    str = [str stringByAppendingString:latitude];
    str = [str stringByAppendingString: @"_"];
    str = [str stringByAppendingString:longitude];
    str = [str stringByAppendingString:@"_"];
    str = [str stringByAppendingString:chosen];
    ////////////
    FastSocket *client = [[FastSocket alloc] initWithHost:@"hackucsc2015.no-ip.info" andPort:@"25565"];
    [client connect];
    
    
    //NSData *data = [(@"post_9_ian_3_3.8_5.2_%@\n",chosen) dataUsingEncoding:NSUTF8StringEncoding];
    
    //NSString* str = [@"post_9_ian_3_3.8_5.2_" stringByAppendingString:chosen];
    //NSData* data = [str dataUsingEncoding:NSUTF8StringEncoding];
    
    //NSString* str = [[[@"post_9_" stringByAppendingString:said ] stringByAppendingString:@"_3_3.8_5.2_" ] stringByAppendingString:chosen];
    
    
    NSLog(@"---------------------%@",str);
    
    NSData* data = [str dataUsingEncoding:NSUTF8StringEncoding];
    
    
    
    NSLog(@"%@",data);
    
    
    
    //NSLog(@"%@",chosen);
    
    //NSData *data = [@"post_9_ian_3_3.8_5.2_Messiah\r" dataUsingEncoding:NSUTF8StringEncoding];
    
    //NSData *data = [@"post_2_ethan_5_3.7_5.4_tubby short\r" dataUsingEncoding:NSUTF8StringEncoding];
    
    //NSData *data = [@"post_8_Katrina_5_3.7_5.4_hot\r" dataUsingEncoding:NSUTF8StringEncoding];
    
    //NSData *data = [@"getall_2.2_3.8_5.2\r" dataUsingEncoding:NSUTF8StringEncoding];
    
    
    long count = [client sendBytes:[data bytes] count:[data length]];
    
    
    
    long expectedLength=64;
    char bytes[expectedLength];
    NSString *received=@"";
    
    //while (![received isEqual:@"\0"]) {
    received = @"";
    [client receiveBytes:bytes count:expectedLength];
    //NSLog(@"%s", bytes);
    
    received = [[NSString alloc] initWithBytes:bytes length:strlen(bytes) encoding:NSUTF8StringEncoding];
    //NSLog(@"%@",received);
    
    
    
    [client close];
        
    }
    
    
    
    NSLog(@"It is empty!");
    
    /*
     long received=0;
     char dataReceived[42];
     while (dataReceived[0]==0) {
     received = [client receiveBytes:dataReceived limit:42];
     
     }
     
     NSLog(@"[%ld] %s",received, dataReceived);
     */

    
    
    
    
    
    
    
    
}


- (void)viewWillAppear {
    
    //[[self tabBarItem] setSelectedImage:[UIImage imageNamed:@"TagIcon.png"]];
    
    //[(UITabBarItem*)[[[self tabBar] items] objectAtIndex:n] setImage:[UIImage imageNamed:@"(unselected image file)"]];
    //[(UITabBarItem*)[[[self tabBar] items] objectAtIndex:n] setSelectedImage:[UIImage imageNamed:@"(selected image file)"]];
    
}


    
    
    




@end
