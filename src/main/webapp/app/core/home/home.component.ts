import { type ComputedRef, defineComponent, inject, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import type LoginService from '@/account/login.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');

    const router = useRouter();

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const openLogin = () => {
      loginService.openLogin();
    };
    console.log('authenticated:', authenticated);

    // Verificar y redirigir cuando el estado de autenticación cambie
    watch(authenticated, newValue => {
      if (newValue) {
        const userRole = loginService.getUserRole();

        const login = username.value;

        if (userRole === 'ROLE_USER') {
          //enviando el id del usuario si encuentra la oficina asociada a el
          router.push('/oficina-user/' + login + '/home'); //trampa
          //router.push('/oficina-user/home'); //trampa
          //router.push(`/oficina-user/${this.user.id}/home`);
        } else if (userRole === 'ROLE_ADMIN') {
          router.push({ name: 'Home' }); // Redirige a la página de admin
        }
      }
    });

    return {
      authenticated,
      username,
      openLogin,
      t$: useI18n().t,
    };
  },
});
