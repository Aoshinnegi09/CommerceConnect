# System design

## Architecture

This application follows a modular monolith architecture. Domain logic is organized into controllers, services, repositories, and DTOs. It is suitable for a portfolio project and can later be split into separate services without a major rewrite.

## Main modules

- Auth and security
- Customer profile management
- Product catalog and inventory
- Order and payment workflow
- Promotion and campaign management
- Notifications and audit
- Dashboard reporting

## Order workflow

1. Customer submits an order.
2. System validates customer and product existence.
3. System checks stock availability for each item.
4. System reduces inventory and creates order items.
5. System persists payment and marks order as confirmed.
6. Customer receives a notification.

## Security model

- JWT token-based authentication
- Password hashing with BCrypt
- Endpoint protection via Spring Security
- Role checks for admin and marketing flows

## Database relationships

- User to Customer: one-to-one
- Category to Product: one-to-many
- Product to Inventory: one-to-one
- Customer to Order: one-to-many
- Order to OrderItem: one-to-many
- Order to Payment: one-to-one
