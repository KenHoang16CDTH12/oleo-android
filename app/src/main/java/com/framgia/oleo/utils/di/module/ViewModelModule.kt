package com.framgia.oleo.utils.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.screen.boxchat.BoxChatViewModel
import com.framgia.oleo.screen.contacts.ContactsViewModel
import com.framgia.oleo.screen.follow.FollowListViewModel
import com.framgia.oleo.screen.followed.FollowedViewModel
import com.framgia.oleo.screen.waiting.WaitingViewModel
import com.framgia.oleo.screen.following.FollowingViewModel
import com.framgia.oleo.screen.forgotpassword.ForgotPasswordViewModel
import com.framgia.oleo.screen.forgotpassword.ResetPasswordViewModel
import com.framgia.oleo.screen.friendrequest.FriendRequestsViewModel
import com.framgia.oleo.screen.home.HomeViewModel
import com.framgia.oleo.screen.location.LocationViewModel
import com.framgia.oleo.screen.login.LoginViewModel
import com.framgia.oleo.screen.main.MainViewModel
import com.framgia.oleo.screen.messages.MessageOptionViewModel
import com.framgia.oleo.screen.messages.MessagesViewModel
import com.framgia.oleo.screen.search.SearchViewModel
import com.framgia.oleo.screen.setting.SettingViewModel
import com.framgia.oleo.screen.signup.SignUpViewModel
import com.framgia.oleo.utils.di.AppViewModelFactory
import com.framgia.oleo.utils.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessagesViewModel::class)
    abstract fun bindMessagesViewModel(messagesViewModel: MessagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    abstract fun bindSignUpViewModel(signUpViewModel: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(settingViewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BoxChatViewModel::class)
    abstract fun bindRoomChatViewModel(boxChatViewModel: BoxChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    abstract fun bindLocationViewModel(locationViewModel: LocationViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendRequestsViewModel::class)
    abstract fun bindFriendRequestsViewModel(friendRequestViewModel: FriendRequestsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowListViewModel::class)
    abstract fun bindFollowListViewModel(followListViewModel: FollowListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowedViewModel::class)
    abstract fun bindFollowedViewModel(followedViewModel: FollowedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowingViewModel::class)
    abstract fun bindFollowingViewModel(followingViewModel: FollowingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WaitingViewModel::class)
    abstract fun bindWaitingViewModel(waitingViewModel: WaitingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageOptionViewModel::class)
    abstract fun bindMessageOptionViewModel(messageOptionViewModel: MessageOptionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    abstract fun bindContactsViewModel(contactsViewModel: ContactsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindForgotPasswordViewModel(forgotPasswordViewModel: ForgotPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel::class)
    abstract fun bindResetPasswordViewModel(resetPasswordViewModel: ResetPasswordViewModel): ViewModel
}
