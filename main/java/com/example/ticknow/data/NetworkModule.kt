package com.example.ticknow.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    //Singleton para una única instancia
    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {
    @Singleton
    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()
}

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//    @Provides
//    fun provideFirestoreManager(@ApplicationContext context: Context): FirestoreManager {
//        return FirestoreManager(context)
//    }
//}
