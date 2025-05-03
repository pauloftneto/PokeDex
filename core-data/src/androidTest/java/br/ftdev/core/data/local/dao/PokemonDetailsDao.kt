package br.ftdev.core.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.ftdev.core.data.local.db.PokemonDatabase
import br.ftdev.core.data.local.entity.PokemonDetailsEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PokemonDetailsDaoTest {

    private lateinit var db: PokemonDatabase
    private lateinit var dao: PokemonDetailsDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PokemonDatabase::class.java)
            .build()
        dao = db.pokemonDetailsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertDetailsAndGetById_returnsCorrectDetails() = runTest {
        val detailsEntity = PokemonDetailsEntity(
            id = 25,
            name = "Pikachu",
            imageUrl = "pika.png",
            height = 4f,
            weight = 60f,
            typesJson = """["electric"]""",
            statsJson = """[{"name":"hp","value":35},{"name":"attack","value":55}]"""
        )

        dao.insertDetails(detailsEntity)
        val retrievedDetails = dao.getDetailsById(25)

        assertThat(retrievedDetails).isNotNull()
        assertThat(retrievedDetails).isEqualTo(detailsEntity)
        assertThat(retrievedDetails?.name).isEqualTo("Pikachu")
        assertThat(retrievedDetails?.typesJson).isEqualTo("""["electric"]""")
        assertThat(retrievedDetails?.statsJson).isEqualTo("""[{"name":"hp","value":35},{"name":"attack","value":55}]""")
    }

    @Test
    @Throws(Exception::class)
    fun insertDetails_withReplaceStrategy_updatesExistingDetails() = runTest {
        val initialDetails = PokemonDetailsEntity(
            id = 4, name = "Charmander", imageUrl = "char_old.png", height = 6f, weight = 85f,
            typesJson = """["fire"]""", statsJson = """[{"name":"speed","value":65}]"""
        )
        val updatedDetails = PokemonDetailsEntity(
            id = 4,
            name = "Charmander",
            imageUrl = "char_new.png",
            height = 6.1f,
            weight = 86f,
            typesJson = """["fire"]""",
            statsJson = """[{"name":"speed","value":70}]"""
        )
        dao.insertDetails(initialDetails)

        dao.insertDetails(updatedDetails)
        val retrievedDetails = dao.getDetailsById(4)

        assertThat(retrievedDetails).isNotNull()
        assertThat(retrievedDetails).isEqualTo(updatedDetails)
        assertThat(retrievedDetails?.imageUrl).isEqualTo("char_new.png")
        assertThat(retrievedDetails?.height).isEqualTo(6.1f)
        assertThat(retrievedDetails?.statsJson).isEqualTo("""[{"name":"speed","value":70}]""")
    }

    @Test
    @Throws(Exception::class)
    fun getDetailsById_returnsNull_whenIdNotFound() = runTest {
        val detailsEntity = PokemonDetailsEntity(
            id = 1, name = "Bulbasaur", imageUrl = "bulba.png", height = 7f, weight = 69f,
            typesJson = """["grass","poison"]""", statsJson = """[{"name":"hp","value":45}]"""
        )
        dao.insertDetails(detailsEntity)

        val retrievedDetails = dao.getDetailsById(999)

        assertThat(retrievedDetails).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun clearAllDetails_removesAllEntries() = runTest {
        val details1 = PokemonDetailsEntity(
            id = 1,
            name = "Bulba",
            height = 0f,
            weight = 0f,
            typesJson = "",
            statsJson = "",
            imageUrl = null
        )
        val details25 = PokemonDetailsEntity(
            id = 25,
            name = "Pika",
            height = 0f,
            weight = 0f,
            typesJson = "",
            statsJson = "",
            imageUrl = null
        )
        dao.insertDetails(details1)
        dao.insertDetails(details25)
        assertThat(dao.getDetailsById(1)).isNotNull()
        assertThat(dao.getDetailsById(25)).isNotNull()

        dao.clearAllDetails()
        val retrievedDetails1 = dao.getDetailsById(1)
        val retrievedDetails25 = dao.getDetailsById(25)

        assertThat(retrievedDetails1).isNull()
        assertThat(retrievedDetails25).isNull()
    }
}