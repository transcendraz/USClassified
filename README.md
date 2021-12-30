# USClassifieds
by 
Chen Tang

Chenhui Zhu 

Justin Kim

Laurence Fong 

Sean Morgenthaler 


## Running the Project

1. Download the ZIP file and unarchive it.
2. Open the project in Android Studio.
3. For the AVD, please select Pixel 3 as the device to emulate on. While the layouts are mostly
constrained and the app will likely look alright on other devices, Pixel 3 is the device we tested on.
4. Because we use Google Maps in the application, download and install Google Play Services by going through
Tools -> SDK Manager -> SDK Tools, and ticking the entries for Google-related installations. Particuarly ensure
that "Google Play services" is enabled. 
5. Build and Run the project through Android Studio. Android Studio should automatically build and download
all the required dependencies.

**NOTE:** Please try this link: http://167.172.197.162/api/v1/items

If this doesn't return a JSON response, then the server is down, and the app will not be functional. 
Please let us know if this happens and we will restart the server.

## Improvements Made in Sprint (Project 2.5)

- Added a Map View to display all items as markers in a map. Each marker can be tapped to show relevant information for the item, and the info window can then be tapped to redirect to that item's detail page.
- Finished the friend functionality, now user can send out friend request, view all pending friend requests, accept or reject a friend request, and view all his/her friends.
- Added a profile photo for users, which can be uploaded optionally during registration.
- Added search item by text functionality. Accomplished through the addition of an "Item Name" filter, which will perform a case-insensitive String match for the title of the item.
- Stablilized filter and sort functionality. Can now reset item list by tapping "Filter" and "Sort" with the corresponding "Reset" Filter/Sorter enabled.
- Renamed the button for marking an item as sold from "Buy it!" to "Sell it!".
- Added item pricture and description in explore page.
