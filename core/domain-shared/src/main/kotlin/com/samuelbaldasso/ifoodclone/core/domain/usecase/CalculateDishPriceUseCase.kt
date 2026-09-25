package com.samuelbaldasso.ifoodclone.core.domain.usecase

import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option

class CalculateDishPriceUseCase {

    /**
     * Calculates the unit and total price for a dish selection according to RN-REST-05 and RN-CART-03:
     * unitPrice = basePrice (or promoPrice) + sum(extraPrice of options)
     * totalPrice = unitPrice * quantity
     */
    operator fun invoke(
        dish: Dish,
        selectedOptions: List<Option>,
        quantity: Int
    ): Money {
        require(quantity in 1..20) { "Quantidade deve ser entre 1 e 20 por item (RN-CART-03), recebido: $quantity" }

        var unitPrice = dish.effectivePrice
        for (option in selectedOptions) {
            unitPrice += option.extraPrice
        }

        return (unitPrice * quantity).coerceAtLeastZero()
    }
}
