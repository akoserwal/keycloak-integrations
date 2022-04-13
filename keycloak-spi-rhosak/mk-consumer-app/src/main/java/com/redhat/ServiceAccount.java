package com.redhat;

import java.time.LocalDateTime;
import java.util.Objects;


public class ServiceAccount {
	public String clientId;
	public String operation;
	public String ip;

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public LocalDateTime created_at;

	public LocalDateTime getLast_update_at() {
		return last_update_at;
	}

	public void setLast_update_at(LocalDateTime last_update_at) {
		this.last_update_at = last_update_at;
	}

	public LocalDateTime last_update_at;

	public String status;
	public Integer count;

	@Override
	public String toString() {
		return "ServiceAccount{" +
				"clientId='" + clientId + '\'' +
				", operation='" + operation + '\'' +
				", ip='" + ip + '\'' +
				", timestamp='" + created_at + '\'' +
				", count=" + count +
				", status='" + status + '\'' +
				'}';
	}

	public static ServiceAccount of(String clientId, String operation, String ip, Integer count, String status, LocalDateTime created_at, LocalDateTime last_update_at){
		ServiceAccount data = new ServiceAccount(clientId, operation, ip, count, status, created_at, last_update_at);
		return data;
	}

	public static ServiceAccount of(String clientId, LocalDateTime last_update_at){
		ServiceAccount data = new ServiceAccount(clientId, last_update_at);
		return data;
	}

	public ServiceAccount(String clientId,LocalDateTime last_update_at) {
		this.clientId = clientId;
		this.last_update_at = last_update_at;
	}

	public ServiceAccount(String clientId, String operation, String ip, Integer count, String status,LocalDateTime created_at, LocalDateTime last_update_at) {
		this.clientId = clientId;
		this.operation = operation;
		this.ip = ip;
		this.created_at = created_at;
		this.last_update_at = last_update_at;
		this.count = count;
		this.status = status;
	}

	public ServiceAccount(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServiceAccount)) return false;
		ServiceAccount that = (ServiceAccount) o;
		return Objects.equals(clientId, that.clientId) && Objects.equals(operation, that.operation) && Objects.equals(ip, that.ip) && Objects.equals(created_at, that.created_at) && Objects.equals(status, that.status) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clientId, operation, ip, created_at, status, count);
	}
}
