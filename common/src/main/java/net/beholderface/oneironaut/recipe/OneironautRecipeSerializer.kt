package net.beholderface.oneironaut.recipe

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.beholderface.oneironaut.Oneironaut

class OneironautRecipeSerializer {
    companion object {
        /**
         * Registered through Architectury instead of a raw [net.minecraft.registry.Registry.register] call.
         * Forge locks the vanilla registries before mod construction, so registering into
         * [RegistryKeys.RECIPE_SERIALIZER] directly throws
         * "Can not register to a locked registry. Modder should use NeoForge Register methods."
         */
        @JvmField
        val DEFERRED: DeferredRegister<RecipeSerializer<*>> =
            DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.RECIPE_SERIALIZER)

        /** Attaches the deferred registrations to the loader's registry event. */
        @JvmStatic
        fun init() {
            DEFERRED.register()
        }

        private val SERIALIZERS: MutableMap<Identifier, RecipeSerializer<*>> = LinkedHashMap()

        val INFUSE: RecipeSerializer<*> = register("infuse", InfusionRecipe.Serializer())

        private fun <T : Recipe<*>?> register(name: String, rs: RecipeSerializer<T>): RecipeSerializer<T> {
            val old = SERIALIZERS.put(Oneironaut.id(name), rs)
            require(old == null) { "Typo? Duplicate id $name" }
            DEFERRED.register(name) { rs }
            return rs
        }
    }
}
