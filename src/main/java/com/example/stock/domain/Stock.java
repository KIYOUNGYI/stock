package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import javax.persistence.Version;

@Entity
@Getter
public class Stock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;

  private Long quantity;

  @Version
  private Long version;

  public Stock() {
  }

  public Stock(Long productId, Long quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  //tc 에서 수량 확인 목적
//  public Long getQuantity() {
//    return quantity;
//  }

  public void decrease(Long quantity) {
    if (this.quantity - quantity < 0) {
      throw new RuntimeException("foo");
    }

    this.quantity = this.quantity - quantity;
  }
}
