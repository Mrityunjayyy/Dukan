# Dukan
---

(EE- 395) ENTREPRENEURSHIP AND VENTURE DEVELOPMENT  
Project Submission   

Mrityunjay (2K20/EE/169)  

## Introduction  
Notable Features of the Application





-	Easy login & registration
-	Detailed product descriptions
-	Product gallery
-	Order summary
-	Shopping cart
-	Sold Product’s List 
-	Adding New products for sale  
  
## About the applciation  
Dukan is an E-commerce android application that allows user to both enlist products for selling and also buy products from the other users. 
We have made use of Firebase is a Backend-as-a-Service (Baas). For IAM authentication we have made use of email id and password. The forget password clickable
textView causes firebase to send password reset email to the user. We have used Bottom Navigation Activity in our project with the four activities namely  
- Dashboard  
- Products  
- Orders  
- Sold Products  
  
1. The dashboard fragment will display all the product’s uploaded by all the active users of the application.
The user would not be able to buy the product uploaded by himself for obvious reason but he can select on 
products uploaded by other uses and have the
functionality to add it in his cart and proceed to checkout once the address has been provided.   

2. In the products fragment the user will be able to see the list of all his products in form of a recycler view. The stock
quantity of a product is automatically updated once a product is sold and will appear in the sold products fragment. 
The user also has the ability to completely remove any of his product from the market if he wishes to do so.   

3. Once the user has gone through the checkout activity all his orders will appear in the orders fragment. 
 Details of the order can be easily accessed by clicking on separate items in the orders list.
In the development stage of this app, we only have cash on delivery method of payment.
The status of order delivery is built on the logic of noting the time at which the order is made and then the status will turn to delivered after 3 hours.   

4. The sold products fragment will display the list of the products of the owner that have been sold along with the details of the buyer including his name, 
address and phone number. In the development stage of this application we have decided to keep the delivery charges a constant i.e ₹100.
Once an order has appeared on the sold products fragment the owner can then use some third party transportation services for the delivery of his product. 



