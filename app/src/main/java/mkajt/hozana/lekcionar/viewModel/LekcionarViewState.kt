package mkajt.hozana.lekcionar.viewModel

import mkajt.hozana.lekcionar.model.dto.MapDTO
import mkajt.hozana.lekcionar.model.dto.PodatkiDTO
import mkajt.hozana.lekcionar.model.dto.RedDTO
import mkajt.hozana.lekcionar.model.dto.SkofijaDTO

sealed class LekcionarViewState {

    object Loading : LekcionarViewState()

    object Loaded : LekcionarViewState()

    object Start : LekcionarViewState()

    data class Data(
        var redovi: List<RedDTO>? = null,
        var skofije: List<SkofijaDTO>? = null,
        var map: List<MapDTO>? = null,
        var podatki: List<PodatkiDTO>? = null,
    ) : LekcionarViewState()
}