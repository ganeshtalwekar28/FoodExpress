export interface DeliveryAgent {
    agentID : string;
    name : string;
    phone : number;
    email : string;
    status : string;
    currentOrderID : number | null;
    todayEarning : number;
    totalEarning : number;
    totalDeliveries : number;
    rating : number;
    orders : {}[];
    id : number;
}