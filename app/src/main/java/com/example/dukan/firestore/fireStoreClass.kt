package com.example.dukan.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.dukan.activities.*
import com.example.dukan.fragments.DashboardFragment
import com.example.dukan.fragments.OrdersFragment
import com.example.dukan.fragments.ProductsFragment
import com.example.dukan.fragments.SoldProductsFragment
import com.example.dukan.models.*
import com.example.dukan.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class fireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity  : RegisterActivity , userInfo : User){
        mFireStore.collection(Constants.USERS).document(userInfo.id).set(userInfo , SetOptions.merge()).addOnSuccessListener{

            activity.userRegistrationSuccess()
        } .addOnFailureListener { exception ->
            activity.hideProgressDialog()
            Log.e(
                activity.javaClass.simpleName , exception.message ,exception)
        }

    }

   fun getCurrentUserID() : String {

        val currentUser  = FirebaseAuth.getInstance().currentUser

        var currentUSerID  : String = ""

        if(currentUser != null) {

        currentUSerID = currentUser.uid
        } else {
            Log.e("iske wajah se hua hai" , "null hai user")
        }

        return currentUSerID

    }

    fun getUserDetails(activity : Activity) {

        mFireStore.collection(Constants.USERS).document(getCurrentUserID()).get().addOnSuccessListener {
            document ->
            Log.i(activity.javaClass.simpleName , document.toString())

            val user = document.toObject(User::class.java)!!

            val sharedPreferences  = activity.getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES , Context.MODE_PRIVATE)

            val editor : SharedPreferences.Editor = sharedPreferences.edit()

            editor.putString(Constants.LOGGED_IN_USERNAME , "${user.firstName} ${user.lastName}")

            editor.apply()

            when (activity) {
               is LoginActivity ->  { activity.userLoggedInSuccess(user) }

                is SettingsActivity -> {
                   activity.userDetailsSuccess(user)
                }
            }
        }  .addOnFailureListener { e ->
            // Hide the progress dialog if there is any error. And print the error in log.
            when (activity) {
                is LoginActivity -> {
                    activity.hideProgressDialog()
                }
                is SettingsActivity -> {
                    activity.hideProgressDialog()
                }
            }

            Log.e(
                activity.javaClass.simpleName,
                "Error while getting user details.",
                e
            )
        }

    }

    fun updateUserProfileData(activity : Activity , userHashMap: HashMap<String , Any>) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap).addOnSuccessListener {
                when(activity){
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }

            } .addOnFailureListener { e ->

                when(activity){
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName , "Error while updating user details" , e )
            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri : Uri? , imageType : String) {
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(imageType + System.currentTimeMillis() + "."  +
                Constants.getFileExtension(activity , imageFileUri!!))


        sRef.putFile(imageFileUri).addOnSuccessListener { TaskSnapshot ->
           Log.e("Firebase image Url :: " , TaskSnapshot.metadata!!.reference!!.downloadUrl.toString() )
            TaskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener {  uri ->
                    Log.e("Download image url is" , uri.toString())
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        } .addOnFailureListener{  exception ->
        when(activity) {
            is UserProfileActivity -> {
                activity.hideProgressDialog()
            }
            is AddProductActivity -> {
                activity.hideProgressDialog()
            }
        }

            Log.e(javaClass.simpleName , exception.message , exception)

        }


    }


    fun uploadProductDetails(activity: AddProductActivity  , productInfo  : Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            } .addOnFailureListener { exception ->

                activity.hideProgressDialog()

                Log.e(javaClass.simpleName , exception.message , exception)

            }
    }

    fun getProductsList(fragment : Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
               Log.e("Products List" , document.documents.toString())
                val productLists : ArrayList<Product> = ArrayList()

                for(i in document.documents){

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productLists.add(product)
                }

                when(fragment) {
                    is ProductsFragment -> {
                        fragment.successProductListsFromFireStore(productLists)
                    }
                }

            }
    }



    fun getDashboardItemsList(fragment  : DashboardFragment) {
          mFireStore.collection(Constants.PRODUCTS)
              .get()
              .addOnSuccessListener { document ->
                  Log.e(fragment.javaClass.simpleName , document.documents.toString() )

                  val productsList : ArrayList<Product> = ArrayList()

                  for(i in document.documents) {

                      val product = i.toObject(Product::class.java)!!
                      product.product_id = i.id
                      productsList.add(product)
                  }

                  fragment.successDashBoardItemsList(productsList)

              } .addOnFailureListener {  exception ->

                  fragment.hideProgressDialog()
                  Log.e(fragment.javaClass.simpleName , exception.message.toString() , exception)

              }
    }


    fun deleteProduct(fragment : ProductsFragment , productId :String) {
        mFireStore.collection(Constants.PRODUCTS).document(productId)
            .delete()
            .addOnSuccessListener {
                     fragment.productDeleteSuccess()
            }.addOnFailureListener { exception ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName , exception.message , exception)

            }
    }

    fun getProductDetails(activity: ProductDetailsActivity , productId : String) {
        mFireStore.collection(Constants.PRODUCTS).document(productId)
            .get()
            .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName , document.toString())

                val product = document.toObject(Product::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            } .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName , exception.message , exception)
            }

    }


    fun addCartItems(activity: ProductDetailsActivity , addToCart  : CartItem ) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart , SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName , exception.message , exception)

            }
    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity , productId : String){
       mFireStore.collection(Constants.CART_ITEMS)
           .whereEqualTo(Constants.USER_ID , getCurrentUserID())
           .whereEqualTo(Constants.PRODUCT_ID , productId)
           .get()
           .addOnSuccessListener { document ->
                      if(document.documents.size > 0 ) {
                          activity.productExistsInCart()
                      } else {
                          activity.hideProgressDialog()
                      }
                Log.v(activity.javaClass.simpleName , document.documents.toString())
           }.addOnFailureListener { exception ->
               activity.hideProgressDialog()
               Log.e(activity.javaClass.simpleName , exception.message , exception)
           }
    }


   fun getCartLists(activity : Activity) {
       mFireStore.collection(Constants.CART_ITEMS)
           .whereEqualTo(Constants.USER_ID , getCurrentUserID())
           .get()
           .addOnSuccessListener { document ->
             // Log.e(activity.javaClass.simpleName , document.documents.toString())
               val list : ArrayList<CartItem> = ArrayList()

               for(i in document.documents) {
                   val cartItem = i.toObject(CartItem::class.java)!!
                   cartItem.id = i.id
                   list.add(cartItem)
               }

               when(activity){
                   is CartListActivity -> {
                       activity.successCartItemsList(list)
                   }

                   is CheckoutActivity -> {
                       activity.successCartItemsList(list)
                   }
               }

           }.addOnFailureListener { exception ->

               when(activity) {
                   is CartListActivity -> {
                       activity.hideProgressDialog()
                   }

                   is CheckoutActivity -> {
                       activity.hideProgressDialog()
                   }
               }

               Log.e(javaClass.simpleName , exception.message , exception)

           }
   }


    fun getAllProductList(activity : Activity){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List" , document.documents.toString())
                val productList : ArrayList<Product> = ArrayList()

                for(i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productList.add(product)
                }
                    when(activity) {
                     is CartListActivity -> {   activity.successProductListsFromFireStore(productList) }

                        is CheckoutActivity -> {
                            activity.successProductListFromFireStore(productList)
                        }
                    }
            }.addOnFailureListener { exception ->
                when(activity) {
                    is CartListActivity -> {  activity.hideProgressDialog() }
                }
            Log.e(javaClass.simpleName , exception.message , exception)

        }

    }

    fun removeItemFromCart(context: Context , cart_id : String){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context) {
                    is CartListActivity -> {
                        context.itemRemoveSuccess()
                    }
                }
            }.addOnFailureListener {  exception ->

                when(context){
                is CartListActivity -> {context.hideProgressDialog() }
                }
                Log.e(javaClass.simpleName , exception.message , exception)
            }
    }

    fun updateMyCart(context : Context , cart_id : String , itemsHashMap : HashMap<String , Any>){
     mFireStore.collection(Constants.CART_ITEMS)
         .document(cart_id)
         .update(itemsHashMap)
         .addOnSuccessListener { document ->
            when(context) {
                is CartListActivity -> {
                    context.itemUpdateSuccess()
                }
            }

         }.addOnFailureListener { exception ->
             when(context) {
                 is CartListActivity -> {
                     context.hideProgressDialog()
                 }
             }
             Log.e(javaClass.simpleName , exception.message , exception)
         }
    }


    fun addAddress(activity : AddEditAddressActivity , addressInfo : Address) {
        mFireStore.collection(Constants.ADDRESSES).document()
            .set(addressInfo , SetOptions.merge())
            .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
            } .addOnFailureListener {  exception ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName , exception.message , exception)

            }
    }



    fun getAllAddressList(activity : AddressListActivity){
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID , getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Addresses List" , document.documents.toString())
                val addressList : ArrayList<Address> = ArrayList()

                for(i in document.documents) {
                    val address  = i.toObject(Address::class.java)
                    address!!.id = i.id

                    addressList.add(address)
                }

                activity. successAddressListsFromFireStore(addressList)

            }.addOnFailureListener { exception ->

                activity.hideProgressDialog()
                Log.e(javaClass.simpleName , exception.message , exception)
            }

    }


    fun updateAddress(activity : AddEditAddressActivity ,addressInfo : Address ,addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(javaClass.simpleName , exception.message , exception)
            }
    }


    fun deleteAddress(activity : AddressListActivity  ,addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                activity.deleteAddressSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(javaClass.simpleName , exception.message , exception)
            }
    }

    fun placeOrder(activity : CheckoutActivity , order : Order){
            mFireStore.collection(Constants.ORDERS)
                .document()
                .set(order , SetOptions.merge())
                .addOnSuccessListener {
                    activity.orderPlacedSuccess()

                } .addOnFailureListener { exception ->
                    activity.hideProgressDialog()
                    Log.e(javaClass.simpleName , exception.message , exception)
                }
    }


    fun updateAllDetails(activity : CheckoutActivity ,cartList : ArrayList<CartItem> , order : Order) {
        val writeBatch = mFireStore.batch()

        for ( cartItem in cartList) {
 //           val productHashMap = HashMap<String, Any>()

//            productHashMap[Constants.STOCK_QUANTITY] =
//                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title ,
                cartItem.price ,
                cartItem.cart_quantity ,
                cartItem.image ,
                order.title ,
                order.order_datetime ,
                order.sub_total_amount ,
                order.shipping_charge ,
                order.total_amount ,
                order.address
            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document(cartItem.product_id)

            writeBatch.set(documentReference , soldProduct)
        }

        for (cartIem in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartIem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit() .addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        } .addOnFailureListener { exception ->
            activity.hideProgressDialog()
            Log.e(javaClass.simpleName , exception.message , exception)

        }
    }

    fun getMyOrdersList(fragment : OrdersFragment){
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID , getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                fragment.hideProgressDialog()
            val list : ArrayList<Order> = ArrayList()

                for ( i in document.documents){
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)

                    fragment.populationOrdersListInUI(list)
                }

            }.addOnFailureListener { exception ->
                fragment.hideProgressDialog()
                Log.e(javaClass.simpleName , exception.message , exception)

            }
    }

    fun getSoldProductsList(fragment : SoldProductsFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID , getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list : ArrayList<SoldProduct> = ArrayList()

                for(i in document.documents) {
                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }

                fragment.successSoldProductLIst(list)

            } .addOnFailureListener { exception ->
            fragment.hideProgressDialog()
            Log.e(javaClass.simpleName , exception.message , exception)

        }
    }
}