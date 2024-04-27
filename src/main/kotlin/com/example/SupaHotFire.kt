package com.example

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

// TODO find a better way to do this
const val supabaseUrl = "hot"
const val supabaseKey = "fire"

val supabase = createSupabaseClient(
    supabaseUrl, supabaseKey
) {
    install(Postgrest)
}


