package com.example

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "hot", // TODO env variables for these
    supabaseKey = "fire"
) {
    install(Postgrest)
}
