package com.framgia.oleo.utils.di.module

import com.framgia.oleo.screen.boxchat.BoxChatFragment
import com.framgia.oleo.screen.home.HomeFragment
import com.framgia.oleo.screen.login.LoginFragment
import com.framgia.oleo.screen.messages.MessagesFragment
import com.framgia.oleo.screen.search.SearchFragment
import com.framgia.oleo.screen.setting.SettingFragment
import com.framgia.oleo.screen.signup.SignUpFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector
    abstract fun contributeSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment

    @ContributesAndroidInjector
    abstract fun contributeBoxFragment(): BoxChatFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}
