package net.beholderface.oneironaut.recipe

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.beholderface.oneironaut.Oneironaut.MOD_ID
import net.beholderface.oneironaut.Oneironaut.id
import net.beholderface.oneironaut.Oneironaut

class OneironautRecipeTypes {
    companion object {
        const val debugMessages = false

        /**
         * Registered through Architectury instead of a raw [net.minecraft.registry.Registry.register] call.
         * Forge locks the vanilla registries before mod construction, so registering into
         * [RegistryKeys.RECIPE_TYPE] directly throws
         * "Can not register to a locked registry. Modder should use NeoForge Register methods."
         */
        @JvmField
        val DEFERRED: DeferredRegister<RecipeType<*>> =
            DeferredRegister.create(MOD_ID, RegistryKeys.RECIPE_TYPE)

        /** Attaches the deferred registrations to the loader's registry event. */
        @JvmStatic
        fun init() {
            DEFERRED.register()
        }

        private val TYPES: MutableMap<Identifier, RecipeType<*>> = LinkedHashMap()

        var INFUSION_TYPE: RecipeType<InfusionRecipe> = registerType("infuse")

        private fun <T : Recipe<*>> registerType(name: String): RecipeType<T> {
            val type: RecipeType<T> = object : RecipeType<T> {
                override fun toString(): String {
                    return "$MOD_ID:$name"
                }
            }
            // never will be a collision because it's a new object
            TYPES[id(name)] = type
            DEFERRED.register(name) { type }
            Oneironaut.boolLogger("Attempting to register type $name, with id ${type.toString()}", debugMessages)
            return type
        }
    }
}
