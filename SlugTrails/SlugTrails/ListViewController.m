//
//  ListViewController.m
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



#import "ListViewController.h"

@interface ListViewController ()
/////////
@property (strong, nonatomic) NSMutableArray *array;
@property (nonatomic, strong) NSMutableArray *Subtext;
////////
@end

@implementation ListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    //
    //// Receiving Data frome database
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
    
    //for(int i = 0; i < 10; i++) {
        
    [INPUT addObject:[NSString stringWithFormat: @"%@",received]];
    //}
    NSLog(@"myArray:\n%@", INPUT);
    
    
    
    
    // The strings are divided into several different strings now
    NSArray *fields = INPUT;
    //}
    fields = [received componentsSeparatedByString:@"&"];
    
    
    //...do something useful with myArrayElement
    //fins=[fields[i] componentsSeparatedByString:@"_"];
    //_array=fins[i];
    //name[i] = fins[1];
    //desc[i] = fins[5];
    
    
    
    
    
    
    
    NSArray *fins;
    NSMutableArray *title = [[NSMutableArray alloc] init];
    NSMutableArray *sub = [[NSMutableArray alloc] init];
    
//      NSLog(@"%d",count);
    
    for (int i = 0; i < [fields count]; i++){
        //...do something useful with myArrayElement
        fins=[fields[i] componentsSeparatedByString:@"_"];
        
//        NSLog(@"%d",[fins count]);
        for (int j=0; j<[fins count]; j++){
            if(j==1) {
                NSLog(fins[j]);
                [title addObject: fins[j]];
//                NSLog(@"++++++%d",_array);
            }
            else if(j==5){
                [sub addObject:fins[j]];
                
            }
        }
    }
    
    
    
    ///////////
    _array = title;
    _Subtext = sub;
    
    
    //NSString *entered=received;
    //[_array insertObject:received atIndex:0];
    //_array = fields;
    NSLog(@"%d",[_array count]);
    
}
    
    
    
    
    ///////////

    
    
//    NSString *entered=received;
//    [_array insertObject:received atIndex:0];
//    _array = [NSMutableArray copy];

   // _array[1]=received;
    //_array = [[NSArray alloc] initWithObjects:@"LOL", @"WE DAH HACKERZ", @"NO Sleep", @"HACKALLNITE", nil];
    //_Subtext = [[NSArray alloc] initWithObjects:@"This is LOL", @"This is WEDAH", @"Fuck Sleep", @"Nite", nil];
    
    
    
    
    
    
    ///////////

    
    
//}
/////////////
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
//    NSLog(@"-------------------------------------------");
    return [_array count];
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSLog(@"SUSUSUSU");

    static NSString *cellID = @"cellID";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
    }
    
    cell.textLabel.text = [_array objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = [_Subtext objectAtIndex:indexPath.row];
    return cell;
}

//////////////












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
