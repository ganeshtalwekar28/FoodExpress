export interface MenuItem {
  id: number;
  name: string;
  price: number;
  image_url: string;
  restaurantId: number;
}

export interface Menu {
  id: number;
  restaurantId: number;
  items: MenuItem[];
}

export interface Item {
  id: number;
  name: string;
  price: number;
  image_url: string;
  originalPrice?: number;
}