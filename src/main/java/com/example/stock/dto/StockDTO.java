package com.example.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class StockDTO {

  private Long id;
  private Long productId;
  private Long quantity;

  public StockDTO() {
  }

  public StockDTO(Long id, Long productId, Long quantity) {
    this.id = id;
    this.productId = productId;
    this.quantity = quantity;
  }
}
