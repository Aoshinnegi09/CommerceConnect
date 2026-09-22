# Test plan

## Unit coverage

- AuthService registration and login logic
- Promotion discount calculations
- Order placement logic and inventory reduction

## Integration checks

- Auth endpoints work with valid credentials
- Product creation works for authorized users
- Order placement rejects insufficient inventory
- Dashboard summary returns counts correctly

## Validation commands

```bash
mvn test
```
