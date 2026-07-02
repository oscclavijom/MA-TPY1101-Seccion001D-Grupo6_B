package com.conectatarot.app.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path


interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/tarotistas")
    suspend fun getTarotistas(@Header("Authorization") token: String): Response<TarotistasResponse>

    @POST("api/sesiones")
    suspend fun agendarSesion(
        @Header("Authorization") token: String,
        @Body request: SesionRequest
    ): Response<SesionResponse>

    @GET("api/sesiones/mis-sesiones")
    suspend fun getMisSesiones(
        @Header("Authorization") token: String
    ): Response<SesionClienteResponse>

    @GET("api/sesiones/mis-sesiones/historial")
    suspend fun getMisSesionesHistorial(
        @Header("Authorization") token: String
    ): Response<SesionClienteResponse>

    @GET("api/sesiones/mis-pagos")
    suspend fun getMisPagos(
        @Header("Authorization") token: String
    ): Response<SesionClienteResponse>

    @GET("api/sesiones/tarotista/historial")
    suspend fun getSesionesTarotistaHistorial(
        @Header("Authorization") token: String
    ): Response<SesionClienteResponse>

    @GET("api/sesiones/tarotista/pagos")
    suspend fun getPagosTarotistaHistorial(
        @Header("Authorization") token: String
    ): Response<SesionClienteResponse>

    @PUT("api/sesiones/{id}/cancelar")
    suspend fun cancelarSesion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @GET("api/sesiones/tarotista")
    suspend fun getSesionesTarotista(
        @Header("Authorization") token: String
    ): Response<SesionTarotistaResponse>

    @PUT("api/sesiones/{id}/confirmar")
    suspend fun confirmarSesion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @PUT("api/sesiones/{id}/rechazar")
    suspend fun rechazarSesion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body request: RegistroRequest
    ): Response<RegistroResponse>

    @POST("api/tarotistas/registro")
    suspend fun registrarTarotista(
        @Body request: RegistroTarotistaRequest
    ): Response<RegistroResponse>

    @GET("api/especialidades")
    suspend fun getEspecialidades(): Response<EspecialidadesResponse>

    @PUT("api/usuarios/{id}")
    suspend fun editarPerfil(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: EditarPerfilRequest
    ): Response<Any>

    @PUT("api/tarotistas/{id}/perfil")
    suspend fun editarPerfilTarotista(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: EditarPerfilTarotistaRequest
    ): Response<Any>

    @PUT("api/tarotistas/mi-perfil")
    suspend fun editarMiPerfilTarotista(
        @Header("Authorization") token: String,
        @Body request: EditarPerfilTarotistaRequest
    ): Response<Any>

    @POST("api/resenas")
    suspend fun crearResena(@Body request: ResenaRequest): Response<Any>

    @GET("api/resenas/tarotista/{id}")
    suspend fun getResenasTarotista(@Path("id") id: Int): Response<ResenasResponse>

    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @POST("api/tarotistas/completar-perfil")
    suspend fun completarPerfilTarotista(
        @Header("Authorization") token: String,
        @Body request: CompletarPerfilRequest
    ): Response<Any>

    @POST("api/pagos/iniciar/{sesionId}")
    suspend fun iniciarPago(@Path("sesionId") sesionId: Int): Response<IniciarPagoResponse>

    @GET("api/pagos/estado/{sesionId}")
    suspend fun estadoPago(@Path("sesionId") sesionId: Int): Response<EstadoPagoResponse>

    @GET("api/admin/dashboard")
    suspend fun getAdminDashboard(
        @Header("Authorization") token: String
    ): Response<AdminDashboardResponse>

    @GET("api/admin/usuarios")
    suspend fun getAdminUsuarios(
        @Header("Authorization") token: String
    ): Response<UsuariosAdminResponse>

    @GET("api/admin/tarotistas/pendientes")
    suspend fun getTarotistasPendientes(
        @Header("Authorization") token: String
    ): Response<TarotistasAdminResponse>

    @PUT("api/admin/tarotistas/{id}/aprobar")
    suspend fun aprobarTarotista(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @PUT("api/admin/usuarios/{id}/bloquear")
    suspend fun bloquearUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @PUT("api/admin/usuarios/{id}/desbloquear")
    suspend fun desbloquearUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @GET("api/admin/pagos")
    suspend fun getAdminPagos(
        @Header("Authorization") token: String
    ): Response<PagosAdminResponse>

    @GET("api/admin/especialidades")
    suspend fun getAdminEspecialidades(
        @Header("Authorization") token: String
    ): Response<EspecialidadesAdminResponse>

    @POST("api/admin/especialidades")
    suspend fun crearEspecialidad(
        @Header("Authorization") token: String,
        @Body request: CrearEspecialidadRequest
    ): Response<EspecialidadAdminMutationResponse>

    @PUT("api/admin/especialidades/{id}")
    suspend fun actualizarEspecialidad(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ActualizarEspecialidadRequest
    ): Response<EspecialidadAdminMutationResponse>

    @DELETE("api/admin/especialidades/{id}")
    suspend fun eliminarEspecialidad(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>
}

data class PagedData(
    val content: List<SesionItem>?,
    val totalElements: Int,
    val totalPages: Int
)
data class LoginRequest(
    val email: String,
    val password: String
)
data class Tarotista(
    val id: Int,
    val nombreProfesional: String,
    val descripcion: String?,
    val precioBase: Double?,
    val estado: String?,
    val especialidades: List<String>?,
    val disponibilidades: List<Disponibilidad>?
)

data class TarotistasResponse(
    val success: Boolean,
    val message: String,
    val data: List<Tarotista>?
)
data class SesionRequest(
    val usuarioId: Int,
    val tarotistaId: Int,
    val especialidadId: Int,
    val fecha: String,
    val duracionMinutos: Int
)

data class SesionResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)

data class SesionClienteResponse(
    val success: Boolean,
    val message: String,
    val data: List<SesionItem>?
)

data class SesionItem(
    val id: Int,
    val nombreCliente: String?,
    val nombreTarotista: String,
    val especialidad: String,
    val fecha: String,
    val duracionMinutos: Int,
    val precioTotal: Double,
    val estado: String,
    val estadoPago: String? = "PENDIENTE"
)

data class SesionTarotistaResponse(
    val success: Boolean,
    val message: String,
    val data: PagedData?
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class RegistroResponse(
    val success: Boolean,
    val message: String
)


data class RegistroTarotistaRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val nombreProfesional: String,
    val descripcion: String,
    val precioBase: Double,
    val especialidades: List<Int>,
    val disponibilidades: List<DisponibilidadRequest>
)

data class DisponibilidadRequest(
    val diaSemana: String,
    val horaInicio: String,
    val horaFin: String
)

data class EspecialidadesResponse(
    val success: Boolean,
    val data: List<Especialidad>?
)

data class Especialidad(
    val id: Int,
    val nombre: String
)

data class EditarPerfilRequest(
    val nombre: String,
    val email: String
)

data class EditarPerfilTarotistaRequest(
    val nombreProfesional: String,
    val descripcion: String,
    val precioBase: Double
)

data class Disponibilidad(
    val diaSemana: String,
    val horaInicio: String,
    val horaFin: String
) : java.io.Serializable

data class ResenaRequest(
    val sesionId: Int,
    val tarotistaId: Int,
    val usuarioId: Int,
    val calificacion: Int,
    val comentario: String
)

data class ResenaItem(
    val id: Int,
    val calificacion: Int,
    val comentario: String?
)

data class ResenasResponse(
    val success: Boolean,
    val data: List<ResenaItem>?,
    val promedio: Double
)

data class GoogleLoginRequest(
    val email: String,
    val nombre: String
)

data class LoginResponse(
    val idUsuario: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val activo: Boolean,
    val token: String,
    val esNuevo: Boolean = false
)

data class CompletarPerfilRequest(
    val usuarioId: Int,
    val nombreProfesional: String,
    val descripcion: String,
    val precioBase: Double,
    val email: String
)

data class IniciarPagoResponse(
    val success: Boolean,
    val url: String?,
    val token: String?
)

data class EstadoPagoResponse(
    val success: Boolean,
    val estadoPago: String?
)

data class RolAdmin(
    val idRol: Int?,
    val nombreRol: String?
)

data class UsuarioAdmin(
    val idUsuario: Int?,
    val nombre: String?,
    val email: String?,
    val activo: Boolean?,
    val rol: RolAdmin?
)

data class UsuariosAdminResponse(
    val success: Boolean,
    val data: List<UsuarioAdmin>?
)

data class TarotistaAdmin(
    val id: Int?,
    val nombreProfesional: String?,
    val estado: String?
)

data class TarotistasAdminResponse(
    val success: Boolean,
    val data: List<TarotistaAdmin>?
)

data class AdminDashboardDTO(
    val usuarios: Long,
    val tarotistas: Long,
    val sesiones: Long,
    val pendientes: Long
)

data class AdminDashboardResponse(
    val success: Boolean,
    val data: AdminDashboardDTO?
)

data class ApiErrorResponse(
    val success: Boolean?,
    val message: String?
)

data class PagoAdmin(
    val id: Int?,
    val idSesion: Int?,
    val nombreCliente: String?,
    val nombreTarotista: String?,
    val monto: Double?,
    val estadoPago: String?,
    val fechaPago: String?
)

data class PagosAdminResponse(
    val success: Boolean,
    val data: List<PagoAdmin>?
)

data class EspecialidadAdmin(
    val id: Int?,
    val nombre: String?,
    val descripcion: String?,
    val activa: Boolean?
)

data class EspecialidadesAdminResponse(
    val success: Boolean,
    val data: List<EspecialidadAdmin>?
)

data class CrearEspecialidadRequest(
    val nombre: String,
    val descripcion: String?
)

data class ActualizarEspecialidadRequest(
    val nombre: String?,
    val descripcion: String?,
    val activa: Boolean?
)

data class EspecialidadAdminMutationResponse(
    val success: Boolean,
    val data: EspecialidadAdmin?,
    val message: String?
)