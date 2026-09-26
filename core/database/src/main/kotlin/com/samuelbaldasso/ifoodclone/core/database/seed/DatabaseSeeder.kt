package com.samuelbaldasso.ifoodclone.core.database.seed

import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity

object DatabaseSeeder {

    fun getRestaurants(): List<RestaurantEntity> = listOf(
        RestaurantEntity(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            rating = 4.5,
            ratingCount = 1420,
            deliveryTimeRange = "30-40 min",
            deliveryFeeCents = 0L,
            minOrderValueCents = 2000L,
            distanceKm = 2.1,
            imageUrl = "https://logodownload.org/wp-content/uploads/2014/07/burger-king-logo-6.png",
            isOpen = true,
            description = "O verdadeiro hambúrguer grelhado no fogo desde 1954.",
            address = "Av. Paulista, 1000 - Bela Vista, São Paulo - SP"
        ),
        RestaurantEntity(
            id = "2",
            name = "Sushibar Oriental",
            category = "Japonesa",
            rating = 4.8,
            ratingCount = 890,
            deliveryTimeRange = "45-55 min",
            deliveryFeeCents = 590L,
            minOrderValueCents = 3500L,
            distanceKm = 3.5,
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&q=80",
            isOpen = true,
            description = "Culinária japonesa contemporânea com peixes frescos selecionados diariamente.",
            address = "R. Tomás Gonzaga, 45 - Liberdade, São Paulo - SP"
        ),
        RestaurantEntity(
            id = "3",
            name = "Pizzaria do Bairro",
            category = "Pizza",
            rating = 4.6,
            ratingCount = 2300,
            deliveryTimeRange = "30-45 min",
            deliveryFeeCents = 0L,
            minOrderValueCents = 4000L,
            distanceKm = 1.8,
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&q=80",
            isOpen = true,
            description = "Pizzas de fermentação natural de 48h, assadas no forno à lenha.",
            address = "R. Treze de Maio, 880 - Bixiga, São Paulo - SP"
        ),
        RestaurantEntity(
            id = "4",
            name = "Açaí Frutas e Cia",
            category = "Doces & Bolos",
            rating = 4.9,
            ratingCount = 3120,
            deliveryTimeRange = "15-25 min",
            deliveryFeeCents = 250L,
            minOrderValueCents = 1500L,
            distanceKm = 1.2,
            imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&q=80",
            isOpen = true,
            description = "O melhor açaí do Pará batido na hora com ingredientes naturais.",
            address = "R. Augusta, 1400 - Consolação, São Paulo - SP"
        ),
        RestaurantEntity(
            id = "5",
            name = "Churrascaria Gaúcha",
            category = "Carnes",
            rating = 4.7,
            ratingCount = 940,
            deliveryTimeRange = "40-55 min",
            deliveryFeeCents = 890L,
            minOrderValueCents = 5000L,
            distanceKm = 4.0,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&q=80",
            isOpen = true,
            description = "Tradicional churrasco no espeto corrido com carnes nobres selecionadas.",
            address = "R. Pamplona, 700 - Jardim Paulista, São Paulo - SP"
        )
    )

    fun getMenuSections(): List<MenuSectionEntity> = listOf(
        // Burger King Sections
        MenuSectionEntity(id = "sec_bk_destaques", restaurantId = "1", name = "Destaques", sortOrder = 1),
        MenuSectionEntity(id = "sec_bk_acompanhamentos", restaurantId = "1", name = "Acompanhamentos", sortOrder = 2),
        MenuSectionEntity(id = "sec_bk_bebidas", restaurantId = "1", name = "Bebidas", sortOrder = 3),

        // Sushibar Sections
        MenuSectionEntity(id = "sec_sushi_combos", restaurantId = "2", name = "Combinados", sortOrder = 1),
        MenuSectionEntity(id = "sec_sushi_entradas", restaurantId = "2", name = "Entradas", sortOrder = 2),

        // Pizzaria Sections
        MenuSectionEntity(id = "sec_pizza_grandes", restaurantId = "3", name = "Pizzas Grandes (8 fatias)", sortOrder = 1),

        // Açaí Sections
        MenuSectionEntity(id = "sec_acai_copos", restaurantId = "4", name = "Açaí no Copo", sortOrder = 1),

        // Churrascaria Sections
        MenuSectionEntity(id = "sec_churrasco_carnes", restaurantId = "5", name = "Carnes Nobres", sortOrder = 1)
    )

    fun getDishes(): List<DishEntity> = listOf(
        // Burger King Dishes
        DishEntity(
            id = "dish_whopper_duplo",
            sectionId = "sec_bk_destaques",
            restaurantId = "1",
            name = "Whopper Duplo",
            description = "Dois suculentos hambúrgueres bovinos grelhados no fogo, queijo derretido, picles, alface fresca, tomate e maionese no pão com gergelim.",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&q=80",
            basePriceCents = 3690L,
            promoPriceCents = 2990L,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),
        DishEntity(
            id = "dish_bk_cheddar",
            sectionId = "sec_bk_destaques",
            restaurantId = "1",
            name = "BK Original Cheddar",
            description = "Pão especial macio, hambúrguer de carne bovina grelhada no fogo, queijo cheddar cremoso e cebola ao molho shoyu.",
            imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400&q=80",
            basePriceCents = 3290L,
            promoPriceCents = 2790L,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 2
        ),
        DishEntity(
            id = "dish_batata_suprema",
            sectionId = "sec_bk_acompanhamentos",
            restaurantId = "1",
            name = "Batata Frita Furiosa",
            description = "Nossa clássica batata frita coberta com molho sabor cheddar derretido e pedaços crocantes de bacon.",
            imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=400&q=80",
            basePriceCents = 1890L,
            promoPriceCents = null,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),
        DishEntity(
            id = "dish_refrigerante_500",
            sectionId = "sec_bk_bebidas",
            restaurantId = "1",
            name = "Refrigerante 500ml",
            description = "Refrescante copo de refrigerante servido bem gelado.",
            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400&q=80",
            basePriceCents = 1290L,
            promoPriceCents = null,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),

        // Sushibar Dishes
        DishEntity(
            id = "dish_combo_salmao",
            sectionId = "sec_sushi_combos",
            restaurantId = "2",
            name = "Combinado Salmão Especial (20 peças)",
            description = "4 sashimis de salmão maçaricado, 4 niguiris salmão trufado, 4 uramakis filadélfia, 4 hossomakis e 4 hot rolls.",
            imageUrl = "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&q=80",
            basePriceCents = 6890L,
            promoPriceCents = 5890L,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),
        DishEntity(
            id = "dish_sunomono",
            sectionId = "sec_sushi_entradas",
            restaurantId = "2",
            name = "Sunomono de Salmão",
            description = "Salada agridoce de pepino japonês fatiado com gergelim torrado e cubos de salmão fresco.",
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&q=80",
            basePriceCents = 1990L,
            promoPriceCents = null,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),

        // Pizzaria Dishes
        DishEntity(
            id = "dish_calabresa",
            sectionId = "sec_pizza_grandes",
            restaurantId = "3",
            name = "Pizza Calabresa Artesanal",
            description = "Molho de tomate italiano, calabresa artesanal defumada fatiada, cebola roxa fresca, azeitonas pretas e orégano.",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&q=80",
            basePriceCents = 5490L,
            promoPriceCents = null,
            isAvailable = true,
            servesPeople = 3,
            sortOrder = 1
        ),

        // Açaí Dishes
        DishEntity(
            id = "dish_acai_500",
            sectionId = "sec_acai_copos",
            restaurantId = "4",
            name = "Copo de Açaí 500ml",
            description = "Açaí cremoso batido com xarope de guaraná tradicional.",
            imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&q=80",
            basePriceCents = 2200L,
            promoPriceCents = null,
            isAvailable = true,
            servesPeople = 1,
            sortOrder = 1
        ),

        // Churrascaria Dishes
        DishEntity(
            id = "dish_picanha",
            sectionId = "sec_churrasco_carnes",
            restaurantId = "5",
            name = "Picanha na Brasa (500g)",
            description = "Corte nobre grelhado no ponto da sua preferência. Acompanha arroz biro-biro, farofa de ovos e vinagrete.",
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&q=80",
            basePriceCents = 8990L,
            promoPriceCents = 7990L,
            isAvailable = true,
            servesPeople = 2,
            sortOrder = 1
        )
    )

    fun getOptionGroups(): List<OptionGroupEntity> = listOf(
        // Whopper Duplo Groups
        OptionGroupEntity(id = "grp_meat_point", dishId = "dish_whopper_duplo", title = "Ponto da Carne", minSelect = 1, maxSelect = 1, isRequired = true, sortOrder = 1),
        OptionGroupEntity(id = "grp_extras", dishId = "dish_whopper_duplo", title = "Adicionais Especiais", minSelect = 0, maxSelect = 4, isRequired = false, sortOrder = 2),
        OptionGroupEntity(id = "grp_removals", dishId = "dish_whopper_duplo", title = "Deseja remover algum ingrediente?", minSelect = 0, maxSelect = 3, isRequired = false, sortOrder = 3),

        // Refri 500ml Groups
        OptionGroupEntity(id = "grp_sabor_refri", dishId = "dish_refrigerante_500", title = "Escolha o sabor", minSelect = 1, maxSelect = 1, isRequired = true, sortOrder = 1),

        // Sushibar Combo Groups
        OptionGroupEntity(id = "grp_molhos_sushi", dishId = "dish_combo_salmao", title = "Molhos inclusos", minSelect = 1, maxSelect = 2, isRequired = true, sortOrder = 1),

        // Pizza Groups
        OptionGroupEntity(id = "grp_borda", dishId = "dish_calabresa", title = "Borda recheada", minSelect = 0, maxSelect = 1, isRequired = false, sortOrder = 1),

        // Açaí Groups
        OptionGroupEntity(id = "grp_acomp_free", dishId = "dish_acai_500", title = "Acompanhamentos Grátis (Escolha até 3)", minSelect = 1, maxSelect = 3, isRequired = true, sortOrder = 1),
        OptionGroupEntity(id = "grp_coberturas_premium", dishId = "dish_acai_500", title = "Coberturas Especiais", minSelect = 0, maxSelect = 2, isRequired = false, sortOrder = 2),

        // Picanha Groups
        OptionGroupEntity(id = "grp_ponto_carne", dishId = "dish_picanha", title = "Ponto da carne", minSelect = 1, maxSelect = 1, isRequired = true, sortOrder = 1)
    )

    fun getOptions(): List<OptionEntity> = listOf(
        // Meat Point
        OptionEntity(id = "opt_point_medium", groupId = "grp_meat_point", name = "Ao ponto", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_point_well", groupId = "grp_meat_point", name = "Bem passado", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),

        // Extras
        OptionEntity(id = "opt_extra_bacon", groupId = "grp_extras", name = "Bacon crocante", extraPriceCents = 450L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_extra_cheddar", groupId = "grp_extras", name = "Queijo cheddar fatiado extra", extraPriceCents = 350L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_extra_onion", groupId = "grp_extras", name = "Cebola caramelizada", extraPriceCents = 300L, isAvailable = true, sortOrder = 3),
        OptionEntity(id = "opt_extra_sauce", groupId = "grp_extras", name = "Molho BK especial", extraPriceCents = 250L, isAvailable = true, sortOrder = 4),

        // Removals
        OptionEntity(id = "opt_rem_pickles", groupId = "grp_removals", name = "Sem picles", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_rem_onion", groupId = "grp_removals", name = "Sem cebola", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_rem_mayo", groupId = "grp_removals", name = "Sem maionese", extraPriceCents = 0L, isAvailable = true, sortOrder = 3),

        // Sabor Refri
        OptionEntity(id = "opt_sabor_coca", groupId = "grp_sabor_refri", name = "Coca-Cola Original", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_sabor_coca_zero", groupId = "grp_sabor_refri", name = "Coca-Cola Sem Açúcar", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_sabor_guarana", groupId = "grp_sabor_refri", name = "Guaraná Antarctica", extraPriceCents = 0L, isAvailable = true, sortOrder = 3),
        OptionEntity(id = "opt_sabor_fanta", groupId = "grp_sabor_refri", name = "Fanta Laranja", extraPriceCents = 0L, isAvailable = true, sortOrder = 4),

        // Molhos Sushi
        OptionEntity(id = "opt_shoyu_trad", groupId = "grp_molhos_sushi", name = "Shoyu Tradicional", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_shoyu_light", groupId = "grp_molhos_sushi", name = "Shoyu Light", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_molho_tare", groupId = "grp_molhos_sushi", name = "Molho Tarê especial", extraPriceCents = 200L, isAvailable = true, sortOrder = 3),

        // Borda Pizza
        OptionEntity(id = "opt_borda_catupiry", groupId = "grp_borda", name = "Borda Catupiry legítimo", extraPriceCents = 990L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_borda_cheddar", groupId = "grp_borda", name = "Borda Cheddar cremoso", extraPriceCents = 890L, isAvailable = true, sortOrder = 2),

        // Açaí Free
        OptionEntity(id = "opt_ac_leite_ninho", groupId = "grp_acomp_free", name = "Leite em pó (Ninho)", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_ac_granola", groupId = "grp_acomp_free", name = "Granola crocante", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_ac_banana", groupId = "grp_acomp_free", name = "Banana fatiada", extraPriceCents = 0L, isAvailable = true, sortOrder = 3),
        OptionEntity(id = "opt_ac_leite_cond", groupId = "grp_acomp_free", name = "Leite condensado", extraPriceCents = 0L, isAvailable = true, sortOrder = 4),
        OptionEntity(id = "opt_ac_pacoca", groupId = "grp_acomp_free", name = "Paçoca rolha", extraPriceCents = 0L, isAvailable = true, sortOrder = 5),

        // Açaí Premium
        OptionEntity(id = "opt_cob_nutella", groupId = "grp_coberturas_premium", name = "Nutella pura", extraPriceCents = 600L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_cob_morango", groupId = "grp_coberturas_premium", name = "Morangos frescos fatiados", extraPriceCents = 450L, isAvailable = true, sortOrder = 2),

        // Picanha Point
        OptionEntity(id = "opt_picanha_mal", groupId = "grp_ponto_carne", name = "Mal passada", extraPriceCents = 0L, isAvailable = true, sortOrder = 1),
        OptionEntity(id = "opt_picanha_ponto", groupId = "grp_ponto_carne", name = "Ao ponto", extraPriceCents = 0L, isAvailable = true, sortOrder = 2),
        OptionEntity(id = "opt_picanha_bem", groupId = "grp_ponto_carne", name = "Bem passada", extraPriceCents = 0L, isAvailable = true, sortOrder = 3)
    )
}
