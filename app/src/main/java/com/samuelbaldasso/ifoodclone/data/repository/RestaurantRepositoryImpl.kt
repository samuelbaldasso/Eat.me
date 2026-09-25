package com.samuelbaldasso.ifoodclone.data.repository

import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.MenuSection
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.OptionGroup
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor() : RestaurantRepository {

    private val sampleRestaurants = listOf(
        Restaurant(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            rating = 4.5,
            ratingCount = 1420,
            deliveryTimeRange = "30-40 min",
            deliveryFee = Money.ZERO,
            minOrderValue = Money(2000L),
            distanceKm = 2.1,
            imageUrl = "https://logodownload.org/wp-content/uploads/2014/07/burger-king-logo-6.png",
            isOpen = true
        ),
        Restaurant(
            id = "2",
            name = "Sushibar Oriental",
            category = "Japonesa",
            rating = 4.8,
            ratingCount = 890,
            deliveryTimeRange = "45-55 min",
            deliveryFee = Money(590L),
            minOrderValue = Money(3500L),
            distanceKm = 3.5,
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&q=80",
            isOpen = true
        ),
        Restaurant(
            id = "3",
            name = "Pizzaria do Bairro",
            category = "Pizza",
            rating = 4.6,
            ratingCount = 2300,
            deliveryTimeRange = "30-45 min",
            deliveryFee = Money.ZERO,
            minOrderValue = Money(4000L),
            distanceKm = 1.8,
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&q=80",
            isOpen = true
        ),
        Restaurant(
            id = "4",
            name = "Açaí Frutas e Cia",
            category = "Doces & Bolos",
            rating = 4.9,
            ratingCount = 3120,
            deliveryTimeRange = "15-25 min",
            deliveryFee = Money(250L),
            minOrderValue = Money(1500L),
            distanceKm = 1.2,
            imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&q=80",
            isOpen = true
        ),
        Restaurant(
            id = "5",
            name = "Churrascaria Gaúcha",
            category = "Carnes",
            rating = 4.7,
            ratingCount = 940,
            deliveryTimeRange = "40-55 min",
            deliveryFee = Money(890L),
            minOrderValue = Money(5000L),
            distanceKm = 4.0,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&q=80",
            isOpen = true
        )
    )

    private val restaurantDetailsMap = mutableMapOf<String, RestaurantDetails>()

    init {
        // 1. Burger King Details
        val bk = sampleRestaurants[0]
        restaurantDetailsMap[bk.id] = RestaurantDetails(
            restaurant = bk,
            description = "O verdadeiro hambúrguer grelhado no fogo desde 1954.",
            address = "Av. Paulista, 1000 - Bela Vista, São Paulo - SP",
            menuSections = listOf(
                MenuSection(
                    id = "sec_destaques",
                    name = "Destaques",
                    dishes = listOf(
                        Dish(
                            id = "dish_whopper_duplo",
                            restaurantId = bk.id,
                            name = "Whopper Duplo",
                            description = "Dois suculentos hambúrgueres bovinos grelhados no fogo, queijo derretido, picles, alface fresca, tomate e maionese no pão com gergelim.",
                            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&q=80",
                            basePrice = Money(3690L),
                            promoPrice = Money(2990L),
                            isAvailable = true,
                            servesPeople = 1,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_meat_point",
                                    title = "Ponto da Carne",
                                    minSelect = 1,
                                    maxSelect = 1,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_point_medium", "Ao ponto", Money.ZERO),
                                        Option("opt_point_well", "Bem passado", Money.ZERO)
                                    )
                                ),
                                OptionGroup(
                                    id = "grp_extras",
                                    title = "Adicionais Especiais",
                                    minSelect = 0,
                                    maxSelect = 4,
                                    isRequired = false,
                                    options = listOf(
                                        Option("opt_extra_bacon", "Bacon crocante", Money(450L)),
                                        Option("opt_extra_cheddar", "Queijo cheddar fatiado extra", Money(350L)),
                                        Option("opt_extra_onion", "Cebola caramelizada", Money(300L)),
                                        Option("opt_extra_sauce", "Molho BK especial", Money(250L))
                                    )
                                ),
                                OptionGroup(
                                    id = "grp_removals",
                                    title = "Deseja remover algum ingrediente?",
                                    minSelect = 0,
                                    maxSelect = 3,
                                    isRequired = false,
                                    options = listOf(
                                        Option("opt_rem_pickles", "Sem picles", Money.ZERO),
                                        Option("opt_rem_onion", "Sem cebola", Money.ZERO),
                                        Option("opt_rem_mayo", "Sem maionese", Money.ZERO)
                                    )
                                )
                            )
                        ),
                        Dish(
                            id = "dish_bk_cheddar",
                            restaurantId = bk.id,
                            name = "BK Original Cheddar",
                            description = "Pão especial macio, hambúrguer de carne bovina grelhada no fogo, queijo cheddar cremoso e cebola ao molho shoyu.",
                            imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400&q=80",
                            basePrice = Money(3290L),
                            promoPrice = Money(2790L),
                            isAvailable = true,
                            servesPeople = 1,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_cheddar_extras",
                                    title = "Adicionais",
                                    minSelect = 0,
                                    maxSelect = 2,
                                    isRequired = false,
                                    options = listOf(
                                        Option("opt_ch_extra_bacon", "Bacon em cubos crocante", Money(450L)),
                                        Option("opt_ch_extra_patty", "Hambúrguer extra grelhado", Money(800L))
                                    )
                                )
                            )
                        )
                    )
                ),
                MenuSection(
                    id = "sec_acompanhamentos",
                    name = "Acompanhamentos",
                    dishes = listOf(
                        Dish(
                            id = "dish_batata_suprema",
                            restaurantId = bk.id,
                            name = "Batata Frita Furiosa",
                            description = "Nossa clássica batata frita coberta com molho sabor cheddar derretido e pedaços crocantes de bacon.",
                            imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=400&q=80",
                            basePrice = Money(1890L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 1
                        ),
                        Dish(
                            id = "dish_onion_rings",
                            restaurantId = bk.id,
                            name = "Onion Rings (10 unid)",
                            description = "Anéis de cebola empanados e dourados com receita exclusiva crocante.",
                            imageUrl = "https://images.unsplash.com/photo-1639024471287-032f66e57989?w=400&q=80",
                            basePrice = Money(1590L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 1
                        )
                    )
                ),
                MenuSection(
                    id = "sec_bebidas",
                    name = "Bebidas",
                    dishes = listOf(
                        Dish(
                            id = "dish_refrigerante_500",
                            restaurantId = bk.id,
                            name = "Refrigerante 500ml",
                            description = "Refrescante copo de refrigerante servido bem gelado com gelo.",
                            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400&q=80",
                            basePrice = Money(1290L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 1,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_sabor_refri",
                                    title = "Escolha o sabor",
                                    minSelect = 1,
                                    maxSelect = 1,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_sabor_coca", "Coca-Cola Original", Money.ZERO),
                                        Option("opt_sabor_coca_zero", "Coca-Cola Sem Açúcar", Money.ZERO),
                                        Option("opt_sabor_guarana", "Guaraná Antarctica", Money.ZERO),
                                        Option("opt_sabor_fanta", "Fanta Laranja", Money.ZERO)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        // 2. Sushibar Details
        val sushi = sampleRestaurants[1]
        restaurantDetailsMap[sushi.id] = RestaurantDetails(
            restaurant = sushi,
            description = "Culinária japonesa contemporânea com peixes frescos selecionados diariamente.",
            address = "R. Tomás Gonzaga, 45 - Liberdade, São Paulo - SP",
            menuSections = listOf(
                MenuSection(
                    id = "sec_combinados",
                    name = "Combinados",
                    dishes = listOf(
                        Dish(
                            id = "dish_combo_salmao",
                            restaurantId = sushi.id,
                            name = "Combinado Salmão Especial (20 peças)",
                            description = "4 sashimis de salmão maçaricado, 4 niguiris salmão trufado, 4 uramakis filadélfia, 4 hossomakis e 4 hot rolls.",
                            imageUrl = "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&q=80",
                            basePrice = Money(6890L),
                            promoPrice = Money(5890L),
                            isAvailable = true,
                            servesPeople = 1,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_molhos_sushi",
                                    title = "Molhos inclusos",
                                    minSelect = 1,
                                    maxSelect = 2,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_shoyu_trad", "Shoyu Tradicional", Money.ZERO),
                                        Option("opt_shoyu_light", "Shoyu Light", Money.ZERO),
                                        Option("opt_molho_tare", "Molho Tarê especial", Money(200L)),
                                        Option("opt_wasabi_extra", "Wasabi extra", Money(150L))
                                    )
                                ),
                                OptionGroup(
                                    id = "grp_hashi",
                                    title = "Talheres descartáveis",
                                    minSelect = 1,
                                    maxSelect = 1,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_hashi_1", "1 Par de Hashis", Money.ZERO),
                                        Option("opt_hashi_2", "2 Pares de Hashis", Money(100L)),
                                        Option("opt_hashi_adaptador", "Hashi com adaptador infantil", Money(200L))
                                    )
                                )
                            )
                        )
                    )
                ),
                MenuSection(
                    id = "sec_entradas",
                    name = "Entradas",
                    dishes = listOf(
                        Dish(
                            id = "dish_sunomono",
                            restaurantId = sushi.id,
                            name = "Sunomono de Salmão",
                            description = "Salada agridoce de pepino japonês fatiado com gergelim torrado e cubos de salmão fresco.",
                            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&q=80",
                            basePrice = Money(1990L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 1
                        )
                    )
                )
            )
        )

        // 3. Pizzaria Details
        val pizza = sampleRestaurants[2]
        restaurantDetailsMap[pizza.id] = RestaurantDetails(
            restaurant = pizza,
            description = "Pizzas de fermentação natural de 48h, assadas no forno à lenha.",
            address = "R. Treze de Maio, 880 - Bixiga, São Paulo - SP",
            menuSections = listOf(
                MenuSection(
                    id = "sec_pizzas",
                    name = "Pizzas Grandes (8 fatias)",
                    dishes = listOf(
                        Dish(
                            id = "dish_calabresa",
                            restaurantId = pizza.id,
                            name = "Pizza Calabresa Artesanal",
                            description = "Molho de tomate italiano, calabresa artesanal defumada fatiada, cebola roxa fresca, azeitonas pretas e orégano.",
                            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&q=80",
                            basePrice = Money(5490L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 3,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_borda",
                                    title = "Borda recheada",
                                    minSelect = 0,
                                    maxSelect = 1,
                                    isRequired = false,
                                    options = listOf(
                                        Option("opt_borda_catupiry", "Borda Catupiry legítimo", Money(990L)),
                                        Option("opt_borda_cheddar", "Borda Cheddar cremoso", Money(890L))
                                    )
                                )
                            )
                        ),
                        Dish(
                            id = "dish_margherita",
                            restaurantId = pizza.id,
                            name = "Pizza Margherita Gourmet",
                            description = "Molho de tomate San Marzano, fatias de mozzarella de búfala, manjericão fresco e azeite extravirgem.",
                            imageUrl = "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=400&q=80",
                            basePrice = Money(5990L),
                            promoPrice = Money(4990L),
                            isAvailable = true,
                            servesPeople = 3
                        )
                    )
                )
            )
        )

        // 4. Açaí Details
        val acai = sampleRestaurants[3]
        restaurantDetailsMap[acai.id] = RestaurantDetails(
            restaurant = acai,
            description = "O melhor açaí do Pará batido na hora com ingredientes naturais.",
            address = "R. Augusta, 1400 - Consolação, São Paulo - SP",
            menuSections = listOf(
                MenuSection(
                    id = "sec_acai_copos",
                    name = "Açaí no Copo",
                    dishes = listOf(
                        Dish(
                            id = "dish_acai_500",
                            restaurantId = acai.id,
                            name = "Copo de Açaí 500ml",
                            description = "Açaí cremoso batido com xarope de guaraná tradicional.",
                            imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&q=80",
                            basePrice = Money(2200L),
                            promoPrice = null,
                            isAvailable = true,
                            servesPeople = 1,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_acomp_free",
                                    title = "Acompanhamentos Grátis (Escolha até 3)",
                                    minSelect = 1,
                                    maxSelect = 3,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_ac_leite_ninho", "Leite em pó (Ninho)", Money.ZERO),
                                        Option("opt_ac_granola", "Granola crocante", Money.ZERO),
                                        Option("opt_ac_banana", "Banana fatiada", Money.ZERO),
                                        Option("opt_ac_leite_cond", "Leite condensado", Money.ZERO),
                                        Option("opt_ac_pacoca", "Paçoca rolha triturada", Money.ZERO)
                                    )
                                ),
                                OptionGroup(
                                    id = "grp_coberturas_premium",
                                    title = "Coberturas Especiais",
                                    minSelect = 0,
                                    maxSelect = 2,
                                    isRequired = false,
                                    options = listOf(
                                        Option("opt_cob_nutella", "Nutella pura", Money(600L)),
                                        Option("opt_cob_morango", "Morangos frescos fatiados", Money(450L)),
                                        Option("opt_cob_choc_branco", "Gotas de chocolate branco", Money(350L))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        // 5. Churrascaria Details
        val churrasco = sampleRestaurants[4]
        restaurantDetailsMap[churrasco.id] = RestaurantDetails(
            restaurant = churrasco,
            description = "Tradicional churrasco no espeto corrido com carnes nobres selecionadas.",
            address = "R. Pamplona, 700 - Jardim Paulista, São Paulo - SP",
            menuSections = listOf(
                MenuSection(
                    id = "sec_carnes",
                    name = "Carnes Nobres",
                    dishes = listOf(
                        Dish(
                            id = "dish_picanha",
                            restaurantId = churrasco.id,
                            name = "Picanha na Brasa (500g)",
                            description = "Corte nobre grelhado no ponto da sua preferência. Acompanha arroz biro-biro, farofa de ovos e vinagrete.",
                            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&q=80",
                            basePrice = Money(8990L),
                            promoPrice = Money(7990L),
                            isAvailable = true,
                            servesPeople = 2,
                            optionGroups = listOf(
                                OptionGroup(
                                    id = "grp_ponto_carne",
                                    title = "Ponto da carne",
                                    minSelect = 1,
                                    maxSelect = 1,
                                    isRequired = true,
                                    options = listOf(
                                        Option("opt_picanha_mal", "Mal passada", Money.ZERO),
                                        Option("opt_picanha_ponto_mal", "Ao ponto para mal", Money.ZERO),
                                        Option("opt_picanha_ponto", "Ao ponto", Money.ZERO),
                                        Option("opt_picanha_bem", "Bem passada", Money.ZERO)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    override suspend fun getRestaurants(): AppResult<List<Restaurant>, AppError> {
        return AppResult.Success(sampleRestaurants)
    }

    override suspend fun getRestaurantDetails(id: String): AppResult<RestaurantDetails, AppError> {
        val details = restaurantDetailsMap[id]
        return if (details != null) {
            AppResult.Success(details)
        } else {
            AppResult.Error(AppError.NotFound("Restaurante com ID '$id' não foi encontrado."))
        }
    }
}