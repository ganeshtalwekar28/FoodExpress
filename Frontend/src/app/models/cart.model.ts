export interface Cartitem {
  cartItemId: number;
  menuItemId: number; 
  name: string;
  price: number;
  quantity: number;
  image_url?: string;
  originalPrice?: number;
}




export interface UserCart {
  cartId?: number;
  id: number;
  restaurantId: number;
  restaurantName: string;
  items: Cartitem[];
  itemCount: number;
  totalAmount: number;
}

