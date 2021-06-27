package com.drug.store.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Order {
  private long orderId;
  private long userId;
  private boolean confirmed;
  private List<Drug> drugs;
}
