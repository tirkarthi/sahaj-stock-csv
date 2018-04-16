## process-orders

```
sahaj.core/process-orders
([orders])
  Process orders.
  If the buy quantity is greater than sell quantity then close all the sell orders and update buy orders status
  If the sell quantity is greater than buy quantity then close all the buy orders and update sell orders status
  If they both buy and sell quantity are equal then close all orders

Spec
  args: (cat :orders (coll-of :sahaj.core/order))
  ret: (cat :orders (coll-of :sahaj.core/order))
```

## open-orders

```
sahaj.core/open-orders
([orders open-total])
  Update orders as OPEN or CLOSED based on remaining orders
Spec
  args: (cat :orders (coll-of :sahaj.core/order) :open-total nat-int?)
  ret: (coll-of :sahaj.core/order)
```

## write-to-file

```
sahaj.core/write-to-file
([orders output])
  Write the orders to the given output file
Spec
  args: (cat :orders (coll-of :sahaj.core/order) :filename string?)
  ret: any?
```

## cumulative-sum

```
sahaj.core/cumulative-sum
([orders])
  Cumulative sum of the quantity of orders
Spec
  args: (cat :orders (coll-of :sahaj.core/order))
  ret: (coll-of number?)
```

## process-file

```
sahaj.core/process-file
([input])
  Process the given CSV file of orders
Spec
  args: (cat :input string?)
  ret: any?
```

## transform-map

```
sahaj.core/transform-map
([orders])
  Coercions for the order map
```

## update-remaining

```
sahaj.core/update-remaining
([orders cumulative-sum])
  Update the cumulative quantity sequence as remaining orders
Spec
  args: (cat :orders (coll-of :sahaj.core/unprocessed-order) :cumulative-sum (coll-of number?))
  ret: (coll-of :sahaj.core/unprocessed-order)
```
