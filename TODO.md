# TODO LIST

## MVP content
### User login
- register as player
- login as player
- logout as player

### User interactions
- player won exp when selling
- player can buy item to their inventory
- player can sell item from their inventory
- player can reroll an item for money

### Cycle & game state related
- populate items to market
- handle schedule ticks on server
- items will decay
- items will be out of market

### Track items price movement
- get price from the current round
- store movements
- retrieve movements



## TO DO NEXT
### Market
- Add transactions to services
- Handle MarketItems cycle
    - populate if missing items
    - add item config limit
    - items TTL
    - add default price
- Buy Item test
- Sell Item test
- Reroll items
- Filtered Market item list
- Cycle
  - Items decay
  - Market price evolution
  - Fetch range of price evolution



## TO KEEP IN MIND
### Transaction within services
- Usage of transactions inside services and not in repos
- Pass entity manager to repository

### DTO
- Check if needs of DTO for requests & queries
- Ajouter les validations via annotations @NotBlank, length etc.
- Utiliser PlayerResponse comme container du User

### Game State
- Add a dedicated table with 1 id and store current round & stuff

### Testing
- Create & add test cases

### Auth
- Use proper auth & session usage for retrieving current player entity

### Exception management
- Add exceptions for generic cases of orElseThrow

### Features
- Fetch valid market items through endpoint
- 