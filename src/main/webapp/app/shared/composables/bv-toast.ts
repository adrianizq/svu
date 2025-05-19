import { getCurrentInstance, type VNode } from 'vue';

interface BvToastOptions {
  title?: string;
  variant?: string;
  solid?: boolean;
  autoHideDelay?: number;
  appendToast?: boolean;
  toaster?: string;
  noCloseButton?: boolean;
  href?: string;
}

interface BvToast {
  toast: (message: string | VNode, options?: BvToastOptions) => void;
}

export function useBvToast() {
  const vm = getCurrentInstance();
  if (!vm) {
    throw new Error('useBvToast must be called within a setup function or a component lifecycle hook.');
  }

  const bvToast = (vm.proxy as any)?.$bvToast as BvToast | undefined;

  if (!bvToast) {
    console.warn('$bvToast is not available. Ensure Bootstrap vue is properly initialized and its Toast plugin is registered');
    return {
      toast: (message: string, options?: BvToastOptions) => {
        console.warn('Mock toast (BootstrapVue $bvToast not found):', message, options);
        alert(`Toast (mock): ${options?.title || ''}\n${message}`);
      },
    };
  }

  return bvToast;
}
