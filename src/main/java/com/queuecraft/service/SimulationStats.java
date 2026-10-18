package com.queuecraft.service;
public record SimulationStats(int generated,int waiting,int serving,int completed,double averageWait,double averageService,int maxQueue){}
