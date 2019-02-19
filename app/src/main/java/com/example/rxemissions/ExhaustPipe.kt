package com.example.rxemissions

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class ExhaustPipe<Z> {
    private val mList = mutableListOf<Z>()
    private val mPipe = Collections.synchronizedList(mList)
    private val mSubject = PublishSubject.create<MutableList<Z>>()
    private var mDisposables = CompositeDisposable()

    fun addSingleItemToPipe(item: Z) {
        mPipe.add(item)
        mSubject.onNext(mutableListOf(item))
    }

    fun addMultipleItemsToPipe(items: MutableList<Z>) {
        mPipe.addAll(items)
        mSubject.onNext(items)
    }


    private var mDisposableZip: Disposable? = null

    fun listToPipeHole(): Observable<in Any> {
        val subscriber = PublishSubject.create<Any>()
        mDisposables.add(mSubject.subscribe {
            mDisposableZip?.let { disposable ->
                disposable.dispose()
                mDisposables.remove(disposable)
            }
            mDisposableZip = Observable.zip(
                Observable.fromIterable(mPipe),
                Observable.interval(1000, TimeUnit.MILLISECONDS),
                object : BiFunction<Z, Long, Any> {
                    override fun apply(processed: Z, interval: Long): Any {
                        mPipe.remove(processed)
                        Log.d("ExhaustPipe", "item $processed processed at $interval milliseconds")
                        Log.d("ExhaustPipe", "Processed item $processed removed from pipe, size : ${mPipe.size}")
                        subscriber.onNext("I'm processing each it at $interval millis periodically")
                        return "SHIT!"
                    }
                }
            ).subscribe()
        })

        return subscriber
    }
}