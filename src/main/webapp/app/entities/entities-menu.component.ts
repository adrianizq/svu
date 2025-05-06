import type AccountService from '@/account/account.service';
import { defineComponent, inject, computed } from 'vue';
import { useI18n } from 'vue-i18n';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EntitiesMenu',
  setup() {
    const i18n = useI18n();
    const accountService = inject<AccountService>('accountService');

    const isAdmin = computed(() => {
      if (!accountService) return false;
      return accountService.hasAnyAuthoritySync(['ROLE_ADMIN']);
    });

    return {
      isAdmin,
      t$: i18n.t,
    };
  },
});
