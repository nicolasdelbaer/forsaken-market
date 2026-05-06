# TODO LIST

## MVP content
### User login
- [x] register as player
- [x] login as player
- [x] logout as player

### User interactions
- [x] player won reput when selling
- [x] player can buy item to their inventory
- [x] player can sell item from their inventory
- [x] player can reroll an item from the market for a price

### Cycle & game state related
- [x] populate items to market
- [x] handle schedule ticks on server
- [x] items will decay
- [x] items will get out of market
- [x] update market OCHL after a cycle of rounds



## TO DO NEXT
### Critical
Ecrire de la doc x_x

### Check

### Market

### Meta
- Sync server & client (send date for auto refresh + data)
- Send data for player exp/reput & display
- Rework responses content (if cost, send back wallet data and so on)

### Project

### Game Rules
- Decay must be very high & based on rarity (utils)
- Time to live based on ranged & rarity (utils)



## IDEAS



## TO KEEP IN MIND
### Refacto
- Refacto Inventory items with proper entries for each action
- Code & project review
- Clean GameStateManager

### DTO
- Add validations via annotations @NotBlank, length etc. from incoming data

### Game State

### Testing
- Add new test cases

### Update theory & must known

### Optimisation
- Clean data based on round id (market evolution, market price)
- Review db accesses & server cached data ()
- MarketOHLC -> Use an interface for getting cached data or db request?
  Pk sur marketPrice: bp_id + round_id (no need for generatedId ?)