package com.vhennus.general.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.vhennus.trade.domain.BuyOrder
import com.vhennus.trade.domain.SellOrder
import kotlinx.serialization.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Serializable
data class SingleSellOrderResp(
    val message:String,
    val data: SellOrder
)

@Serializable
data class GenericResp<T>(
    val message: String,
    val server_message: String?,
    val data:T?
)

class GenericRespAdapter<T> : JsonDeserializer<GenericResp<T>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GenericResp<T> {
        Log.d("ADAPTER DESERIALIZE", "true")
        val jsonObject = json.asJsonObject
        val message = jsonObject.get("message").asString
        val serverMessage = jsonObject.get("server_message")?.asString
        val dataType = (typeOfT as ParameterizedType).actualTypeArguments[0] // Extract generic type
        val data: T? = jsonObject.get("data")?.let { context.deserialize<T>(it, dataType) }

        return GenericResp(message, serverMessage, data)
    }

}
class GenericRespAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        Log.d("GenericRespAdapterFactory", "🛠 Checking type: ${type.type}")

        if (type.rawType != GenericResp::class.java) {
            Log.d("GenericRespAdapterFactory", "❌ Skipping, not GenericResp")
            return null
        }

        Log.d("GenericRespAdapterFactory", "✅ Handling GenericResp...")

        val dataType = (type.type as ParameterizedType).actualTypeArguments[0]
        val dataAdapter: TypeAdapter<Any> = gson.getAdapter(TypeToken.get(dataType)) as TypeAdapter<Any>

        val adapter = object : TypeAdapter<GenericResp<Any>>() {
            override fun write(out: JsonWriter, value: GenericResp<Any>?) {
                Log.d("GenericRespAdapter", "✅ Serializing GenericResp...")
                out.beginObject()

                out.name("message")
                if (value?.message == null) out.nullValue() else out.value(value.message)

                out.name("server_message")
                if (value?.server_message == null) out.nullValue() else out.value(value.server_message)

                out.name("data")
                if (value?.data == null) {
                    out.nullValue() // ✅ Handle `null` properly
                } else {
                    dataAdapter.write(out, value.data)
                }

                out.endObject()
            }




            override fun read(`in`: JsonReader): GenericResp<Any> {
                Log.d("GenericRespAdapter", "✅ Deserializing GenericResp...")

                val jsonObject = JsonParser.parseReader(`in`).asJsonObject

                val message = jsonObject.get("message")?.takeIf { it !is JsonNull }?.asString
                val serverMessage = jsonObject.get("server_message")?.takeIf { it !is JsonNull }?.asString

                val dataElement = jsonObject.get("data")?.takeIf { !it.isJsonNull }
                val data: Any? = if (dataElement != null) {
                    if (dataElement.isJsonArray) {
                        // If `data` is a list, parse each item safely, keeping nulls if needed
                        dataElement.asJsonArray.map { item ->
                            if (item.isJsonNull) null else dataAdapter.fromJsonTree(item)
                        }
                    } else {
                        // If `data` is a single object
                        dataAdapter.fromJsonTree(dataElement)
                    }
                } else {
                    null
                }



                return GenericResp(message.toString(), serverMessage, data)
            }


        }


        Log.d("GenericRespAdapterFactory", "✅ Returning TypeAdapter: $adapter")

        @Suppress("UNCHECKED_CAST")
        return adapter as TypeAdapter<T>
    }
}


data class CancelSellOrderResp(
    val message: String,
    val data:String
)


@Serializable
data class MyBuyOrdersResp(
    val message: String,
    val data:List<BuyOrder>
)

@Serializable
data class SingleBuyOrdersResp(
    val message: String,
    val data:BuyOrder
)

