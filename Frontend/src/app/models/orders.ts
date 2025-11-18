import { Cartitem } from './cart.model';

export interface Orders {

    id?: number;
    orderID: number;
    status: string;
    restaurantName: string;
    pickupAddress: any; 
    customerName : string;
    dropAddress : string; 
    items: Cartitem[];
    totalItems: number;
    totalAmount: number;
    orderDate: string;
    agentName : string;   
}