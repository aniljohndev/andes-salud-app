package ar.com.andessalud.andes.fabricas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.principal;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class fabrica_WS {
    public static void APPBuscarGrupoFamiliar(final Context context, final String idAfiliado
            , final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

        url=url+"APPBuscarGrupoFamiliar";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            Log.d("1311", "" + xmlToJson);
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);


                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar el grupo familiar:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar el grupo familiar:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar el grupo familiar:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idAfiliado", idAfiliado);
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }




    public static void APPBuscarTipoInternacionAPP(final Context context, final String idAfiliado
            , final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

        url=url+"WSAPPBuscarTipoInternacionAPP";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);

                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar los tipos de internación:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los tipos de internación:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar los tipos de internación:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idAfiliado", idAfiliado);
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarPrestacionesAMB(final Context context , final Map<String, String> params, final  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarPrestacionesAMB";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar la especialidad:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar la especialidad:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar la especialidad:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarPrestadorPorPrestacion(final Context context , final Map<String, String> params
            , final  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarPrestadorPorPrestacion";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("datares",response);
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar los prestadores:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los prestadores:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar los prestadores:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPSolicitarOrdenDeConsulta(final Context context , final Map<String, String> params
            , final  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPSolicitarOrdenDeConsulta";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("10")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al evaluar la orden ambulatoria:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPRegistrarRegID(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPRegistrarRegID";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("03")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Error al intentar enviar el ID de la APP:\n\tCódigo de retorno inválido");
                            }
                        }catch (Exception errEx){
                            errorHandler.onError("Error desconocido al intentar enviar el ID de la APP:\n\t"+errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        }else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido intentar enviar el ID de la APP:\n"+error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPConfirmarOrdenDeConsultaAUD(final Context context , final Map<String, String> params
            , final  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPConfirmarOrdenDeConsultaAUD";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al confirmar la orden ambulatoria auditada:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al confirmar la orden ambulatoria auditada:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la orden ambulatoria auditada:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPConfirmarOrdenDeConsultaAUT(final Context context , final Map<String, String> params
            , final  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPConfirmarOrdenDeConsultaAUT";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al confirmar la orden ambulatoria:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al confirmar la orden ambulatoria:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la orden ambulatoria:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonUltActualizacion(final Context context,final Map<String, String> params ,  final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonUltActualizacion";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Hubo un error al sincronizar el buzón, es posible que su buzón este desactualizado.\n\tError: Código de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Hubo un error al sincronizar el buzón, es posible que su buzón este desactualizado.\n\tError:" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar el buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError("Hubo un error de servidor al sincronizar el buzón, es posible que su buzón este desactualizado. Por favor vuelva a intentar mas tarde");
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar el buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else {
                            errorHandler.onError("Error desconocido al sincronizar el buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo:\n\tError:" + error);
                        }
                        return;
                    }
                }){

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonActualizarORDENAMB(final Context context,final Map<String, String> params ,  final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonActualizarORDENAMB";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Hubo un error al sincronizar las ordenes ambulatorias del buzón, es posible que su buzón este desactualizado.\n\tError: Código de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Hubo un error al sincronizar las ordenes ambulatorias del buzón, es posible que su buzón este desactualizado.\n\tError:" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar las ordenes ambulatorias del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError("Hubo un error de servidor al sincronizar las ordenes ambulatorias del buzón, es posible que su buzón este desactualizado. Por favor vuelva a intentar mas tarde");
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar las ordenes ambulatorias del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else {
                            errorHandler.onError("Error desconocido al sincronizar el buzón, las ordenes ambulatorias del posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo:\n\tError:" + error);
                        }
                        return;
                    }
                }){

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonActualizarCREDPROV(final Context context,final Map<String, String> params ,  final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonActualizarCREDPROV";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Hubo un error al sincronizar las credenciales virtuales del buzón, es posible que su buzón este desactualizado.\n\tError: Código de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Hubo un error al sincronizar las  credenciales virtuales del buzón, es posible que su buzón este desactualizado.\n\tError:" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar credenciales virtuales del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError("Hubo un error de servidor al sincronizar las credenciales virtuales del buzón, es posible que su buzón este desactualizado. Por favor vuelva a intentar mas tarde");
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar las  credenciales virtuales del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else {
                            errorHandler.onError("Error desconocido al sincronizar el buzón, las  credenciales virtuales del posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo:\n\tError:" + error);
                        }
                        return;
                    }
                }){

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonActualizarNOTIFICACIONES(final Context context,final Map<String, String> params ,  final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonActualizarNOTIFICACIONES";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Hubo un error al sincronizar las notificaciones del buzón, es posible que su buzón este desactualizado.\n\tError: Código de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Hubo un error al sincronizar las nhotificaciones del buzón, es posible que su buzón este desactualizado.\n\tError:" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar las notificaciones del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError("Hubo un error de servidor al sincronizar las notificaciones del buzón, es posible que su buzón este desactualizado. Por favor vuelva a intentar mas tarde");
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError("Hubo un error de conexión al sincronizar las notificaciones del buzón, es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else {
                            errorHandler.onError("Error desconocido al sincronizar el buzón, las notificaciones es posible que su buzón este desactualizado. Por favor verifique su conexion a internet y vuelva a intentarlo:\n\tError:" + error);
                        }
                        return;
                    }
                }){

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarPrestadoresCartillaBusquedas(final Context context,final Map<String, String> params ,  final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarPrestadoresCartillaBusquedas";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else if (valor.substring(0, 2).equals("10")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            }else {
                                errorHandler.onError("Hubo un error al buscar los datos del mapa.\n\tError: Código de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Hubo un error al buscar los datos del mapa.\n\tError:" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError("Hubo un error de conexión al buscar los datos del mapa. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError("Hubo un error de servidor al buscar los datos del mapa. Por favor vuelva a intentar mas tarde");
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError("Hubo un error de conexión al buscar los datos del mapa. Por favor verifique su conexion a internet y vuelva a intentarlo");
                        } else {
                            errorHandler.onError("Error desconocido al buscar los datos del mapa. Por favor verifique su conexion a internet y vuelva a intentarlo:\n\tError:" + error);
                        }
                        return;
                    }
                }){

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarGuardiaActivaMacroZonas(final Context context, final String idAfiliado, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarGuardiaActivaMacroZonas";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar la zona:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar la zona:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar la zona:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idAfiliado", idAfiliado);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarGuardiaActivaZonas(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarGuardiaActivaZonas";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar la zona:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar la zona:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar la zona:\n" + error);
                        }
                        return;
                    }                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarGuardiaActivaEnZona(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.srvLocProduccion);;

        url=url+"APPBuscarGuardiaActivaEnZona";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar los prestadores:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los prestadores:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar los prestadores:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPSolicitarOrdenGuardiaActiva(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

        url=url+"APPSolicitarOrdenGuardiaActiva";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al intentar asignar la guardia activa:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al asignar la guardia activa:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al asignar la guardia activa:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtieneEmergencias(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

        url=url+"APPObtieneEmergencias";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al intentar actualizar los teléfonos de emergencia:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al actualizar los teléfonos de emergencia:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al actualizar los teléfonos de emergencia:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarCartillaPrestadoresPracticas(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarCartillaPrestadoresPracticas";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar el prestador para estudios médicos:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar el prestador para estudios médicos:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar el prestador para estudios médicos:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPSolicitarPractica(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPSolicitarPractica";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar el prestador para estudios médicos:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar el prestador para estudios médicos:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar el prestador para estudios médicos:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarDelegacionesExpendio(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarDelegacionesExpendio";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar las delegaciones para la nueva credencial:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar las delegaciones para la nueva credencial:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar las delegaciones para la nueva credencial:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPAgregarCredencialNueva(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPAgregarCredencialNueva";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")
                                || valor.substring(0, 2).equals("02")
                                || valor.substring(0, 2).equals("03")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al confirmar para la nueva credencial:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al confirmar la nueva credencial:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la nueva credencial:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPConfirmaCredencialNuevaCambiaSede(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPConfirmaCredencialNuevaCambiaSede";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al confirmar el cambio de delegación de la nueva credencial:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al confirmar el cambio de delegación de la nueva credencial:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la nueva credencial:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscaDatoCredencial(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscaDatoCredencial";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso );
                            } else {
                                errorHandler.onError("Error al buscar el dato de la credencial provisoria:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar el dato de la credencial provisoria:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar el dato de la credencial provisoria:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPConfirmarCredencialProvisoria(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPConfirmarCredencialProvisoria";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al confirmar la credencial provisoria:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al confirmar la credencial provisoria:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la credncial provisoria:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPInformarInternacionTitular(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPInformarInternacionTitular";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al informar la internación:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al informar la internación:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la credncial provisoria:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerFormulariosEspeciales(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerFormulariosEspeciales";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar los formularios especiales:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los formularios especiales:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al confirmar la credncial provisoria:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPPresentarDocumentacion(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPPresentarDocumentacion";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al enviar la documentación:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al enviar la documentación:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al enviar la documentación:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPPresentarReintegro(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPPresentarReintegro";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al solicitar el reintegro:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al solicitar el reintegro:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido solicitar el reintegro:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPInformarIncGF(final Context context, final Map<String, String> params ,final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPInformarIncGF";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al informar la incorporación del grupo familiar:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al informar la incorporación del grupo familiar:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al informar la incorporación del grupo familiar:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerCartillasBusqueda(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerCartillasBusqueda";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("10")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar las cartillas médicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar las cartillas médicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));

                        }else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar las cartillas médicas:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void WEBGENCentroComunicacionesLlamar(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBGENCentroComunicacionesLlamar";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("10")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar las cartillas médicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar las cartillas médicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));

                        }else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar las cartillas médicas:\n" + error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarEspecialidadesTurnos(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarPrestacionesAMB";
//        url=url+"APPBuscarEspecialidadesTurnos";
//        commenting for changes.

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("datad",response);
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar la especialidad:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar la especialidad:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar la especialidad:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarConsultoriosTurnos(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

//        url=url+"APPBuscarConsultoriosTurnos";
        url=url+"APPBuscarPrestadorPorPrestacion";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("datarest",response);
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar los centros:\n\tCódigo de retorno inválido");
                            }
                        }catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los centros:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        }else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        }else{
                            errorHandler.onError("Error desconocido al solicitar el centro:\n"+error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarTurnosLibres(final Context context, final Map<String, String> params, final SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarTurnosLibres";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successHandler.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar los centros:\n\tCódigo de retorno inválido");
                            }
                        }catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar los centros:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        }else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        }else{
                            errorHandler.onError("Error desconocido al solicitar el centro:\n"+error);
                        }
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPSolicitarTurnoConCoseguro(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPSolicitarTurnoConCoseguro";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00") || valor.substring(0, 2).equals("10")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            } else if (valor.substring(0, 2).equals("10")) {
                                String mensaje=valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                String mensajeAUD=valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                String convenioAUD=valores.getElementsByTagName("convenio").item(0).getTextContent();
                                String prestacionAUD=valores.getElementsByTagName("prestacion").item(0).getTextContent();
                                String domRenglon1AUD=valores.getElementsByTagName("domRenglon1").item(0).getTextContent();
                                String domRenglon2AUD=valores.getElementsByTagName("domRenglon2").item(0).getTextContent();
                                String codAutorizacionAUD=valores.getElementsByTagName("codAutorizacion").item(0).getTextContent();
                                String fecVencimientoAUD=valores.getElementsByTagName("fecVencimiento").item(0).getTextContent();
                                String coseguro=valores.getElementsByTagName("coseguro").item(0).getTextContent();
                                return;
                            } else {
                                errorListener.onError("Error al evaluar la solicitud de órden de consulta:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorListener.onError("Error desconocido al evaluar la solicitud de órden de consulta:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error desconocido al evaluar la solicitud de órden de consulta:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPConfirmarTurnoAUT(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPConfirmarTurnoAUT";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al evaluar la solicitud del turno:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al confirmar el turno:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error desconocido al confirmar el turno:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonActualizarTURNOS(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonActualizarTURNOS";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al actualizar los turnos del buzón de avisos :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al actualizar los turnos del buzón de avisos:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al actualizar los turnos del buzón de avisos:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPCancelarTurno(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPCancelarTurno";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al cancelar el turno del buzón de avisos :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al cancelar el turno del buzón de avisos:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al cancelar el turno del buzón de avisos:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPAgregarReclamo(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPAgregarReclamo";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al enviar el reclamo/sugerencia :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al enviar el reclamo/sugerencia:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al enviar el reclamo/sugerencia:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerPreguntasFrecuentes(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerPreguntasFrecuentes";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las preguntas frecuentes :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las preguntas frecuentes:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las preguntas frecuentes:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerRespuestaFrecuente(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerRespuestaFrecuente";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las preguntas frecuentes :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las preguntas frecuentes:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las preguntas frecuentes:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerDelegaciones(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerDelegaciones";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las sedess :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las sedes:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las sedes:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTBuscarAfiliadoGeneral(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTBuscarAfiliadoGeneral";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los afiliados :\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los afiliados:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los afiliados:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTBuscarPrestacionOdonto(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTBuscarPrestacionOdonto";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las prestaciones odontológicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las prestaciones odontológicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las prestaciones odontológicas:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTBuscarPrestacionOdontoDientes(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTBuscarPrestacionOdontoDientes";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las piezas odontológicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las piezas odontológicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las piezas odontológicas:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTBuscarPrestacionOdontoCaras(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTBuscarPrestacionOdontoCaras";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las caras odontológicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las caras odontológicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las caras odontológicas:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTBuscarPrestacionOdontoCoseguro(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTBuscarPrestacionOdontoCoseguro";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar el coseguro de la prestación odontológica:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar el coseguro de la prestación odontológica:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las caras odontológicas:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerFarmaciasMacroZona(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerFarmaciasMacroZona";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las zonas de la cartilla de farmacia:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las zonas de la cartilla de farmacia:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las zonas de la cartilla de farmacia:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerFarmaciasZona(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerFarmaciasZona";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar la zona de la cartilla de farmacia:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar la zona de la cartilla de farmacia:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar la zona de la cartilla de farmacia:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarFarmaciasCartilla(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarFarmaciasCartilla";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las farmacias de la zona de la cartilla de farmacia:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las farmacias de la zona de la cartilla de farmacia:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las farmacias de la zona de la cartilla de farmacia:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarConsultoriosTurnosProfesional(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarConsultoriosTurnosProfesional";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar el centro con prestador:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar el centro con prestador:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar el centro con prestador:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarTurnosLibresPrestadorCentro(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuscarTurnosLibresPrestadorCentro";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar el centro con prestador:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar el centro con prestador:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar el centro con prestador:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerDatosDeContacto(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerDatosDeContacto";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseInfo",response);
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            Log.d("responseHandler",""+xmlToJson);
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos de contacto:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos de contacto:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos de contacto:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerProvincias(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerProvincias";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos de las provincias:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos de las provincias:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos de las provincias:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerLocalidades(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerLocalidades";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos de las localidades:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos de las localidades:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos de las localidades:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPActualizaDatosContactoFijo(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPActualizaDatosContactoFijo";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al almacenar los datos de contacto:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al almacenar los datos de contacto:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al almacenar los datos de contacto:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPObtenerEstadoAfiliacion(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPObtenerEstadoAfiliacion";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseInfo",response);
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            Log.d("responseHandlerrs",""+xmlToJson);

                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los estados de afiliación:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los estados de afiliación:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al los estados de afiliación:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuzonActualizarORDENPRAC(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPBuzonActualizarORDENPRAC";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseInfo",response);
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar las ordenes de prácticas del buzón:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar las ordenes de prácticas del buzón:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar las ordenes de prácticas del buzón:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPBuscarDrOnlineLeerEspecialidad(final Context context, final String idAfiliado
            , final SuccessResponseHandler successResponseHandler, final ErrorResponseHandler errorHandler ) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);

        url=url+"WEBAPPDrOnlineLeerEspecialidades";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successResponseHandler.onSuccess(valores);

                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorHandler.onError(msgAviso);
                            } else {
                                errorHandler.onError("Error al buscar las especialidades de Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errorHandler.onError("Error desconocido al buscar las especialidades de Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorHandler.onError("Error desconocido al buscar las especialidades de Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idAfiliado", idAfiliado);
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineLeerEspecialidadesProfesional(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineLeerEspecialidadesProfesional";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos de los prestadores en Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos de los prestadores en Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos del detalle de la especialidad en Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineLeerEspecialidadesProfesionalHorarios(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineLeerEspecialidadesProfesionalHorarios";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos de los horarios del prestador en Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos de los horarios del prestador en Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos del detalle de los horarios del prestador en Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineLeerEspecialidadesCuestionario(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineLeerEspecialidadesCuestionario";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al buscar los datos del detalle de la especialidad en Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al buscar los datos del detalle de la especialidad en Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al buscar los datos del detalle de la especialidad en Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineActualizaSalaEspera(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineActualizaSalaEspera";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al actualizar la sala de espera de Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al actualizar la sala de espera de Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al actualizar la sala de espera de Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineRefrescaSalaEspera(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineRefrescaSalaEspera";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al refrescar la sala de espera de Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al refrescar la sala de espera de Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al refrescar la sala de espera de Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPDrOnlineCancelaSalaEspera(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        if (!hayConexionARed(context)){
            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAPPDrOnlineCancelaSalaEspera";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al cancelar la sala de espera de Dr. Online:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al cancelar la sala de espera de Dr. Online:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al cancelar la sala de espera de Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void WEBAgregarOrdenOdonto(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"WEBAgregarOrdenOdonto";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")
                                || valor.substring(0, 2).equals("10")
                                    || valor.substring(0, 2).equals("15")
                            ) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al solicitar la orden de odontológica:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al solicitar la orden odontológica:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al refrescar la sala de espera de Dr. Online:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void APPEXTRptPrestacionesOdonto(final Context context, final Map<String, String> params, final SuccessResponseHandler<Document> successListener, final ErrorResponseHandler errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url =context.getResources().getString(R.string.srvLocProduccion);
        url=url+"APPEXTRptPrestacionesOdonto";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                successListener.onSuccess(valores);
                            } else if (valor.substring(0, 2).equals("01")) {
                                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                errorListener.onError(msgAviso);
                            }else {
                                errorListener.onError("Error al armar el reporte de prestaciones odontologicas:\n\tCódigo de retorno inválido");
                            }
                        } catch (Exception errEx) {
                            errEx.printStackTrace();
                            errorListener.onError("Error desconocido al armar el reporte de prestaciones odontologicas:\n\t" + errEx.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorConexion));
                        } else if (error.getClass().equals(ServerError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorServidor));
                        }else if (error.getClass().equals(NoConnectionError.class)) {
                            errorListener.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                        } else {
                            errorListener.onError("Error general al armar el reporte de prestaciones odontologicas:\n" + error);
                        }
                        return;
                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }



    public static void AppDrSpeciality(final Context context,  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="https://digitalech.com/doctor-panel/api/get-medical-specialties";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,null, response -> {

            //                      Document valores = funciones.getDomElement(response);
            successHandler.onSuccess(response);

            Log.d("checkingasd", String.valueOf(response));

            Log.d("checkingasd", String.valueOf(1133));


                                     },
                error -> {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                    } else if (error.getClass().equals(ServerError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                    }else if (error.getClass().equals(NoConnectionError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                    } else {
                        errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n" + error);
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
//                    headers.put("Cache-Control", "application/x-www-form-urlencoded");//put your token here
                headers.put("Authorization", "Bearer " + principal.access_token);

//            Content-Type;//put your token here

                return headers;


            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }


    public static void displayDoctor(final Context context,  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler ,String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="https://digitalech.com/doctor-panel/api/get-doctors/"+id;
        Log.d("urldoctordata",url);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,null, response -> {

            //                      Document valores = funciones.getDomElement(response);
            successHandler.onSuccess(response);

            Log.d("checkingdoctore", String.valueOf(response));

            Log.d("checkingasd", String.valueOf(1133));


        },
                error -> {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                    } else if (error.getClass().equals(ServerError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                    }else if (error.getClass().equals(NoConnectionError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                    } else {
                        errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n" + error);
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
//                    headers.put("Cache-Control", "application/x-www-form-urlencoded");//put your token here
                headers.put("Authorization", "Bearer " + principal.access_token);

//            Content-Type;//put your token here

                return headers;


            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void displayDoctorSchedule(final Context context,  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler ,String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="https://digitalech.com/doctor-panel/api/get-doctor-schedule/"+id;
        Log.d("urldoctordata",url);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,null, response -> {

            //                      Document valores = funciones.getDomElement(response);
            successHandler.onSuccess(response);

            Log.d("checkingdoctore", String.valueOf(response));

//            Log.d("checkingasd", String.valueOf(1133));


        },
                error -> {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                    } else if (error.getClass().equals(ServerError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                    }else if (error.getClass().equals(NoConnectionError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                    } else {
                        errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n" + error);
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
//                    headers.put("Cache-Control", "application/x-www-form-urlencoded");//put your token here
                headers.put("Authorization", "Bearer " + principal.access_token);

//            Content-Type;//put your token here

                return headers;


            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public static void getWaitingRoomList(final Context context,  SuccessResponseHandler successHandler, final ErrorResponseHandler errorHandler ,int id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="https://digitalech.com/doctor-panel/api/app-waiting-room/"+id
        ;
        Log.d("urldoctordata",url);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,null, response -> {

            //                      Document valores = funciones.getDomElement(response);
            successHandler.onSuccess(response);

            Log.d("response09seop", String.valueOf(response));

//            Log.d("checkingasd", String.valueOf(1133));


        },
                error -> {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorConexion));
                    } else if (error.getClass().equals(ServerError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorServidor));
                    }else if (error.getClass().equals(NoConnectionError.class)) {
                        errorHandler.onError(context.getResources().getString(R.string.msgErrorSinConexion));
                    } else {
                        errorHandler.onError("Error desconocido al evaluar la orden ambulatoria:\n" + error);
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
//                    headers.put("Cache-Control", "application/x-www-form-urlencoded");//put your token here
                headers.put("Authorization", "Bearer " + principal.access_token);

//            Content-Type;//put your token here

                return headers;


            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }




    public static Boolean hayConexionARed(Context contexto) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
