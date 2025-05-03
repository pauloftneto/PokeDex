package br.ftdev.core.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.ftdev.core.data.local.db.PokemonDatabase
import br.ftdev.core.data.local.entity.PokemonEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class PokemonDaoTest {

    private lateinit var db: PokemonDatabase
    private lateinit var dao: PokemonDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PokemonDatabase::class.java)
            .build()
        dao = db.pokemonDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAllAndGetPokemonList_returnsCorrectPaginatedList() = runTest {
        val allPokemon = List(25) {
            PokemonEntity(
                id = it + 1,
                name = "Pokemon${it + 1}",
                imageUrl = "img${it + 1}"
            )
        }
        dao.insertAll(allPokemon)

        val firstPage = dao.getPokemonList(limit = 10, offset = 0)

        assertThat(firstPage).hasSize(10)
        assertThat(firstPage.first().id).isEqualTo(1)
        assertThat(firstPage.last().id).isEqualTo(10)

        val secondPage = dao.getPokemonList(limit = 10, offset = 10)

        assertThat(secondPage).hasSize(10)
        assertThat(secondPage.first().id).isEqualTo(11)
        assertThat(secondPage.last().id).isEqualTo(20)

        val thirdPage = dao.getPokemonList(limit = 10, offset = 20)

        assertThat(thirdPage).hasSize(5)
        assertThat(thirdPage.first().id).isEqualTo(21)
        assertThat(thirdPage.last().id).isEqualTo(25)

        val emptyPage = dao.getPokemonList(limit = 10, offset = 25)

        assertThat(emptyPage).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_withReplaceStrategy_updatesExistingItems() = runTest {
        val initialList = listOf(
            PokemonEntity(id = 1, name = "Bulbasaur", imageUrl = "img1_old"),
            PokemonEntity(id = 2, name = "Ivysaur", imageUrl = "img2")
        )
        val updatedList = listOf(
            PokemonEntity(id = 1, name = "Bulba Updated", imageUrl = "img1_new"),
            PokemonEntity(id = 3, name = "Venusaur", imageUrl = "img3")
        )
        dao.insertAll(initialList)

        dao.insertAll(updatedList)
        val finalList = dao.getPokemonList(limit = 5, offset = 0)

        assertThat(finalList).hasSize(3)
        assertThat(finalList).containsExactly(
            PokemonEntity(id = 1, name = "Bulba Updated", imageUrl = "img1_new"),
            PokemonEntity(id = 2, name = "Ivysaur", imageUrl = "img2"),
            PokemonEntity(id = 3, name = "Venusaur", imageUrl = "img3")
        ).inOrder()
    }

    @Test
    @Throws(Exception::class)
    fun clearAll_removesAllEntriesFromTable() = runTest {
        val pokemonList = List(5) {
            PokemonEntity(
                id = it + 1,
                name = "Pokemon${it + 1}",
                imageUrl = "img${it + 1}"
            )
        }
        dao.insertAll(pokemonList)
        val initialCount = dao.count()
        assertThat(initialCount).isEqualTo(5)

        dao.clearAll()
        val finalCount = dao.count()
        val listAfterClear = dao.getPokemonList(limit = 10, offset = 0)

        assertThat(finalCount).isEqualTo(0)
        assertThat(listAfterClear).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun count_returnsCorrectNumberOfEntries() = runTest {
        val pokemonList1 =
            List(5) { PokemonEntity(id = it + 1, name = "P${it + 1}", imageUrl = null) }
        val pokemonList2 =
            List(3) { PokemonEntity(id = it + 6, name = "P${it + 6}", imageUrl = null) }

        var currentCount = dao.count()
        assertThat(currentCount).isEqualTo(0)

        dao.insertAll(pokemonList1)
        currentCount = dao.count()
        assertThat(currentCount).isEqualTo(5)

        dao.insertAll(pokemonList2)
        currentCount = dao.count()
        assertThat(currentCount).isEqualTo(8)

        dao.clearAll()
        currentCount = dao.count()
        assertThat(currentCount).isEqualTo(0)
    }
}