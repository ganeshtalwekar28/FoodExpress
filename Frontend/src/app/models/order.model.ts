import { Cartitem } from './cart.model';

export interface Order {
    id?: number;
    orderID: number;
    userID: string;
    items: Cartitem[];
    totalAmount: number;
    totalItems: number;
    status: string;
    image_url: string;
    deliveryAddress: any;
    paymentMethod: number;
    orderDate: string;
    restaurantName: string;
    razorpayOrderId?: string;
}
