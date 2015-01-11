# Used to create dumby data for SlugTrails
import random
import cStringIO

fo = open("db.data", "wb")

animals = {0:'Deer', 1:'Banana Slug', 2:'Squirrel', 3:'Coyote', 4:'Mountain Lion', 5:'Bunny', 6:'Raccoon', 7:'Wild Stoner', 8:'Hawk', 9:'Bat'}
discritp = {0:'Eating all of our grass', 1:'Mythical', 2:'In a tree', 3:'Help me', 4:'I should call 911', 5:'Fluffy bunny', 6:'Steeling food', 7:'Bruh', 8:'Huge bird', 9:'Grey skin'}
count = 0;
output = cStringIO.StringIO()

for i in range(10):
	for j in range(random.randrange(2,18,1)):
		count = count + 1
		time = random.randrange(0, 360, 1)
		ran1 = (random.randrange(-6500, 50000, 1))*.00001
		ran2 = (random.randrange(-19000, 90000, 1))*.00001
		x = (37.0000+ran1)
		y = (-122.0600+ran2)
		Data = str(count)+"_"+animals[i]+"_"+str(time)+"_"+str(x)+"_"+str(y)+"_"+discritp[i]
		fo.write(Data);	
		fo.write("\r\n");

fo.close()