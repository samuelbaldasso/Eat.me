package com.samuelbaldasso.ifoodclone.core.domain.usecase

import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Option

sealed interface DishValidationResult {
    data object Valid : DishValidationResult
    data class Invalid(val groupErrors: Map<String, String>) : DishValidationResult
}

class ValidateDishSelectionUseCase {

    /**
     * Validates dish option selections against RN-REST-05 business rules.
     * @param dish The dish being customized.
     * @param selectedOptionsByGroup Map of OptionGroup.id to set of selected Option IDs.
     */
    operator fun invoke(
        dish: Dish,
        selectedOptionsByGroup: Map<String, Set<String>>
    ): DishValidationResult {
        if (!dish.isAvailable) {
            return DishValidationResult.Invalid(mapOf("dish" to "Este item está indisponível no momento."))
        }

        val errors = mutableMapOf<String, String>()

        for (group in dish.optionGroups) {
            val selectedOptionIds = selectedOptionsByGroup[group.id].orEmpty()
            val count = selectedOptionIds.size

            if (group.isRequired && count < group.minSelect) {
                errors[group.id] = "Selecione pelo menos ${group.minSelect} opção(ões) obrigatória(s)."
                continue
            }

            if (count < group.minSelect) {
                errors[group.id] = "Selecione no mínimo ${group.minSelect} opção(ões)."
                continue
            }

            if (count > group.maxSelect) {
                errors[group.id] = "Selecione no máximo ${group.maxSelect} opção(ões)."
                continue
            }

            // Verify if selected options are actually available
            val groupOptionsMap = group.options.associateBy { it.id }
            for (optionId in selectedOptionIds) {
                val option = groupOptionsMap[optionId]
                if (option == null || !option.isAvailable) {
                    errors[group.id] = "Uma das opções selecionadas não está mais disponível."
                    break
                }
            }
        }

        return if (errors.isEmpty()) DishValidationResult.Valid else DishValidationResult.Invalid(errors)
    }
}
