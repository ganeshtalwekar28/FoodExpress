import { Component, OnInit } from '@angular/core';
import { DeliveryAgent } from '../models/delivery-agent';
import { AdminService } from '../admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agent-details',
  standalone: false,
  templateUrl: './agent-details.component.html',
  styleUrl: './agent-details.component.css'
})
export class AgentDetailsComponent implements OnInit {

  
  agents: DeliveryAgent[] = [];

  
  constructor(private router : Router, private adminService: AdminService) { }

  
  
  ngOnInit(): void {
    
    this.adminService.getAllAgents().subscribe((data: DeliveryAgent[]) => {
      this.agents = this.sortByStatusPriority(data);
    });
  }

  
  
  viewAgent(id: number): void {
    this.router.navigate(['/agent-dashboard', id]);
  }

  
  
  sortByStatusPriority(data: DeliveryAgent[]): DeliveryAgent[] {
    
    const statusPriority: { [key: string]: number } = {
      'busy': 1,
      'available': 2
    };
    return data.sort((a, b) => {
      const aPriority = statusPriority[(a.status || '').toLowerCase()] || 99;
      const bPriority = statusPriority[(b.status || '').toLowerCase()] || 99;
      return aPriority - bPriority;
    });
  }

  
  
  getAvatarColor(name: string): string {
    const colors = ['#007bff', '#28a745', '#17a2b8', '#ffc107', '#dc3545'];
    
    const index = name.charCodeAt(0) % colors.length;
    return colors[index];
  }
}