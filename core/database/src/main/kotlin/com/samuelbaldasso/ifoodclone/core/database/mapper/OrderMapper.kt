package com.samuelbaldasso.ifoodclone.core.database.mapper

import com.samuelbaldasso.ifoodclone.core.database.entity.OrderEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.OrderItemWithOptions
import com.samuelbaldasso.ifoodclone.core.database.relation.OrderWithItems
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItemOption
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderItem
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import java.util.UUID

fun OrderWithItems.toDomain(): Order {
    return Order(
        id = order.id,
        restaurantId = order.restaurantId,
        restaurantName = order.restaurantName,
        restaurantImageUrl = order.restaurantImageUrl,
        items = items.map { it.toDomain() },
        subtotal = Money(order.subtotalCents),
        deliveryFee = Money(order.deliveryFeeCents),
        discount = Money(order.discountCents),
        total = Money(order.totalCents),
        status = try {
            OrderStatus.valueOf(order.status)
        } catch (e: Exception) {
            OrderStatus.PLACED
        },
        paymentMethod = try {
            PaymentMethod.valueOf(order.paymentMethod)
        } catch (e: Exception) {
            PaymentMethod.PIX
        },
        deliveryAddress = OrderAddress(
            street = order.street,
            number = order.number,
            neighborhood = order.neighborhood,
            city = order.city,
            state = order.state,
            complement = order.complement,
            reference = order.reference
        ),
        createdAtMillis = order.createdAtMillis,
        estimatedDeliveryMinutes = order.estimatedDeliveryMinutes
    )
}

fun OrderItemWithOptions.toDomain(): OrderItem {
    return OrderItem(
        id = item.id,
        dishId = item.dishId,
        dishName = item.dishName,
        unitPrice = Money(item.unitPriceCents),
        quantity = item.quantity,
        totalPrice = Money(item.totalPriceCents),
        selectedOptions = options.map {
            CartItemOption(
                optionId = it.optionId,
                name = it.name,
                extraPrice = Money(it.extraPriceCents)
            )
        },
        notes = item.notes
    )
}

fun createOrderEntitiesFromCart(
    cart: Cart,
    paymentMethod: PaymentMethod,
    address: OrderAddress,
    discount: Money,
    orderId: String = "ord_${UUID.randomUUID().toString().take(8)}"
): Triple<OrderEntity, List<OrderItemEntity>, List<OrderItemOptionEntity>> {
    val subtotal = cart.subtotal
    val deliveryFee = cart.deliveryFee
    val total = (subtotal + deliveryFee - discount).coerceAtLeastZero()

    val orderEntity = OrderEntity(
        id = orderId,
        restaurantId = cart.restaurant?.id ?: "",
        restaurantName = cart.restaurant?.name ?: "",
        restaurantImageUrl = cart.restaurant?.imageUrl,
        subtotalCents = subtotal.cents,
        deliveryFeeCents = deliveryFee.cents,
        discountCents = discount.cents,
        totalCents = total.cents,
        status = OrderStatus.CONFIRMED.name,
        paymentMethod = paymentMethod.name,
        street = address.street,
        number = address.number,
        neighborhood = address.neighborhood,
        city = address.city,
        state = address.state,
        complement = address.complement,
        reference = address.reference,
        createdAtMillis = System.currentTimeMillis(),
        estimatedDeliveryMinutes = 35
    )

    val itemEntities = mutableListOf<OrderItemEntity>()
    val optionEntities = mutableListOf<OrderItemOptionEntity>()

    cart.items.forEach { cartItem ->
        val itemId = "oi_${UUID.randomUUID().toString().take(8)}"
        itemEntities.add(
            OrderItemEntity(
                id = itemId,
                orderId = orderId,
                dishId = cartItem.dishId,
                dishName = cartItem.dishName,
                unitPriceCents = cartItem.unitPrice.cents,
                quantity = cartItem.quantity,
                totalPriceCents = cartItem.totalPrice.cents,
                notes = cartItem.notes
            )
        )

        cartItem.selectedOptions.forEach { opt ->
            optionEntities.add(
                OrderItemOptionEntity(
                    orderItemId = itemId,
                    optionId = opt.optionId,
                    name = opt.name,
                    extraPriceCents = opt.extraPrice.cents
                )
            )
        }
    }

    return Triple(orderEntity, itemEntities, optionEntities)
}
