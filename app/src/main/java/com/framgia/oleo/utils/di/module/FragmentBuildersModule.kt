package com.framgia.oleo.utils.di.module

import com.framgia.oleo.screen.boxchat.BoxChatFragment
import com.framgia.oleo.screen.follow.FollowListFragment
import com.framgia.oleo.screen.followed.FollowedFragment
import com.framgia.oleo.screen.followed.FollowingFragment
import com.framgia.oleo.screen.followed.WaitingFragment
import com.framgia.oleo.screen.friendrequest.FriendRequestsFragment
import com.framgia.oleo.screen.home.HomeFragment
import com.framgia.oleo.screen.location.LocationFragment
import com.framgia.oleo.screen.login.LoginFragment
import com.framgia.oleo.screen.messages.MessageOptionFragment
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

    @ContributesAndroidInjector
    abstract fun contributeLocationFragment(): LocationFragment

    @ContributesAndroidInjector
    abstract fun contributeMessageOptionFragment(): MessageOptionFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendRequestsFragment(): FriendRequestsFragment

    @ContributesAndroidInjector
    abstract fun contributeFollowListFragment(): FollowListFragment

    @ContributesAndroidInjector
    abstract fun contributeFollowedFragment(): FollowedFragment

    @ContributesAndroidInjector
    abstract fun contributeFollowingFragment(): FollowingFragment

    @ContributesAndroidInjector
    abstract fun contributeWaitingFragment(): WaitingFragment
}
