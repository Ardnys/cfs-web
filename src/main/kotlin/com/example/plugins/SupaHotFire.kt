package com.example.plugins

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

private val supabaseUrl: String = System.getenv("SUPABASE_URL") ?: "fire"
private val supabaseKey: String = System.getenv("SUPABASE_KEY") ?: "hot"

val supabase = createSupabaseClient(
    supabaseUrl, supabaseKey
) {
    install(Postgrest)
    install(Realtime)
}


