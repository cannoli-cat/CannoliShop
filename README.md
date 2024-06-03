A simple shop plugin custom made for WoC.
Requires MythicMobs as a dependency.

# How to create a shop:
1. Place a sign on a chest.
   
   2. First line should always be [price] to indicate creation of a shop.
      
      3. Second line is the price of anything in the chest. Must be a valid integer.
         
         4. Third line can be any valid minecraft material or MythicItem.

# Admin Shops:
### An admin shop allows for all items within the shop to be purchased *infinitely*, the buyer will still be charged accordingly, however no payment will be placed in the chest.

## How to create an admin shop:
1. Enter admin mode via the command displayed below.
  
   2. Follow steps 1-4 in "How to create a shop".
## Commands:
   1. /cshop <sub>(requires cannnolishop.command)</sub>
   
      - Admin <sub>(requires cannolishop.admin)</sub>
         - Entering admin mode will allow the player to remove or edit shops as if they were the *owner* of that shop.
         - Also allows for the creation of an 'admin' shop.
         - Usage: /cshop admin
      
      - Show <sub>(requires cannolishop.show)</sub>
         - Will display all of the targeted players owned shops in a GUI. 
         - Usage: /cshop show (player) 
      
## Permissions:
   cannolishop.command
   
   cannolishop.show
   
   cannolishop.admin
